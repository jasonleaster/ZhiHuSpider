package org.jasonleaster.spiderz.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * Author: jasonleaster
 * Date  : 2017/5/18
 * Email : jasonleaster@gmail.com
 * Description:
 */
public final class JedisPoolUtils {

    private static final Logger log = Logger.getLogger(JedisPoolUtils.class);

    private static final Properties redisProperty = Resources.getResourceAsProperties("redis.properties");

    private static JedisPoolUtils instance;

    private static JedisPool jedisPool;

    public static JedisPoolUtils getInstance(){
        if (instance != null) {
            return instance;
        }

        synchronized (JedisPoolUtils.class){
            instance = new JedisPoolUtils();

            int maxActive = Integer.valueOf(redisProperty.getProperty("redis.pool.maxActive"));
            int maxIdle   = Integer.valueOf(redisProperty.getProperty("redis.pool.maxIdle"));
            long maxWait  = Integer.valueOf(redisProperty.getProperty("redis.pool.maxWait"));
            String host   = redisProperty.getProperty("redis.host");

            if (maxActive > 0 && maxIdle > 0 && maxWait > 0 && host != null && !host.isEmpty()) {
                // 创建jedis池配置实例
                JedisPoolConfig config = new JedisPoolConfig();

                // 设置池配置项值
                config.setMaxTotal(maxActive);
                config.setMaxIdle(maxIdle);
                config.setMaxWaitMillis(maxWait);
                config.setTestOnBorrow(true);
                config.setTestOnReturn(true);

                // 根据配置文件,创建shared池实例
                jedisPool = new JedisPool(config, host);
            }else{
                log.error("Error Parameters of configuration!");
                return null;
            }
        }

        return instance;
    }

    /**
     * 执行器， 它保证在执行操作之后释放数据源returnResource(jedis)
     */
    private abstract class Executor<T> {

        JedisPool jedisPool;
        Jedis jedis;
        int dbIndex;

        Executor(JedisPool jedisPool, int dbIndex) {
            this.dbIndex   = dbIndex;
            this.jedisPool = jedisPool;
            this.jedis     = jedisPool.getResource();
        }

        // call back function
        abstract T execute();

        final T getResult() {
            T result = null;
            try {
                // 选择db索引
                jedis.select(dbIndex);

                result = execute();
            } catch (Throwable e) {
                throw new RuntimeException("Redis execute exception", e);
            } finally {
                if (null != jedis) {
                    jedis.close();
                }
            }
            return result;
        }
    }

    public synchronized Long addMessagesIntoQueue(final int dbIndex, final String queueName, final List<String> values){
        return new Executor<Long>(jedisPool, dbIndex) {
            @Override
            Long execute() {
                // 入队
                if (values.size() <= 0){
                    return -1L;
                }

                String[] valuesInArray = new String[values.size()];
                values.toArray(valuesInArray);
                return jedis.rpush(queueName, valuesInArray);
            }
        }.getResult();
    }

    public synchronized Long addMessagesIntoQueue(final int dbIndex, final String queueName, final int limitedQueueLen, final List<String> values){

        long currentQueueLen = getListLen(dbIndex, queueName);

        if (currentQueueLen > limitedQueueLen){
            log.error("Exceed the limitation of the length of queue!");
            return -1L;
        }

        return addMessagesIntoQueue(dbIndex, queueName, values);
    }

    public String getMessageFromQueue(final int dbIndex, final String key){
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                // 出队
                return jedis.lpop(key);
            }
        }.getResult();
    }

    public synchronized Long getListLen(final int dbIndex, final String key){
        return new Executor<Long>(jedisPool, dbIndex) {
            @Override
            Long execute() {
                return jedis.llen(key);
            }
        }.getResult();
    }

    /**
     * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， setString 就覆写旧值，无视类型。
     * 对于某个原本带有生存时间（TTL）的键来说， 当setString 成功在这个键上执行时， 这个键原有的
     * TTL 将被清除。 时间复杂度：O(1)
     *
     * @param dbIndex redis数据库索引
     * @param key key
     * @param value string value
     * @return 在设置操作成功完成时，才返回 OK 。
     */
    public String setString(final int dbIndex, final String key, final String value) {
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                return jedis.set(key, value);
            }
        }.getResult();
    }

    /**
     * 将值 value 关联到 key ，并将 key 的生存时间设为 expire (以秒为单位)。
     * 如果 key 已经存在， 将覆写旧值。 类似于以下两个命令:
     * SET key value
     * EXPIRE key expire # 设置生存时间 不同之处是这个方法是一个原子性(atomic)操作，
     * 关联值和设置生存时间两个动作会在同一时间内完成，在 Redis 用作缓存时，非常实用。
     * 时间复杂度：O(1)
     *
     * @param key key
     * @param value string value
     * @param expire 生命周期
     * @return 设置成功时返回 OK 。当 expire 参数不合法时，返回一个错误。
     */
    public String setString(final int dbIndex, final String key, final String value, final int expire) {
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                return jedis.setex(key, expire, value);
            }
        }.getResult();
    }


    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在。若给定的 key 已经存在，
     * 则 setStringIfNotExists 不做任何动作。
     * 时间复杂度：O(1)
     *
     * @param key key
     * @param value string value
     * @return 设置成功，返回 1 。设置失败，返回 0 。
     */
    public Long setStringIfNotExists(final int dbIndex, final String key, final String value) {
        return new Executor<Long>(jedisPool, dbIndex) {
            @Override
            Long execute() {
                return jedis.setnx(key, value);
            }
        }.getResult();
    }

    /**
     * 返回 key 所关联的字符串值。如果 key 不存在那么返回特殊值 nil 。
     * 假如 key 储存的值不是字符串类型，返回一个错误，因为 getString 只能用于处理字符串值。
     * 时间复杂度: O(1)
     *
     * @param key key
     * @return 当 key 不存在时，返回 nil ，否则，返回 key 的值。如果 key 不是字符串类型，那么返回一个错误。
     */
    public String getString(final int dbIndex, final String key) {
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                return jedis.get(key);
            }
        }.getResult();
    }


    /* ======================================Hashes====================================== */

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 hashSet 操作。
     * 如果域 field已经存在于哈希表中，旧值将被覆盖。 时间复杂度: O(1)
     *
     * @param key key
     * @param field 域
     * @param value string value
     * @return
     *      如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。
     *      如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
     */
    public Long hashSet(final int dbIndex, final String key, final String field, final String value) {
        return new Executor<Long>(jedisPool, dbIndex) {
            @Override
            Long execute() {
                return jedis.hset(key, field, value);
            }
        }.getResult();
    }

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 hashSet 操作。
     * 如果域 field已经存在于哈希表中，旧值将被覆盖。
     *
     * @param key key
     * @param field 域
     * @param value string value
     * @param expire 生命周期，单位为秒
     * @return
     *      如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。
     *      如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
     */
    public Long hashSet(final int dbIndex, final String key, final String field, final String value, final int expire) {
        return new Executor<Long>(jedisPool, dbIndex) {
            @Override
            Long execute() {
                Pipeline pipeline = jedis.pipelined();
                Response<Long> result = pipeline.hset(key, field, value);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。 时间复杂度:O(1)
     *
     * @param key key
     * @param field 域
     * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    public String hashGet(final int dbIndex, final String key, final String field) {
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                return jedis.hget(key, field);
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。 如果哈希表 key 存在，同时设置这个 key 的生存时间
     *
     * @param key key
     * @param field 域
     * @param expire 生命周期，单位为秒
     * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    public String hashGet(final int dbIndex, final String key, final String field, final int expire) {
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                Pipeline pipeline = jedis.pipelined();
                Response<String> result = pipeline.hget(key, field);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。 时间复杂度: O(N) (N为fields的数量)
     *
     * @param key key
     * @param hash field-value的map
     * @return 如果命令执行成功，返回 OK 。当 key 不是哈希表(hash)类型时，返回一个错误。
     */
    public String hashMultipleSet(final int dbIndex, final String key, final Map<String, String> hash) {
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                return jedis.hmset(key, hash);
            }
        }.getResult();
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。同时设置这个 key 的生存时间
     *
     * @param key key
     * @param hash field-value的map
     * @param expire 生命周期，单位为秒
     * @return 如果命令执行成功，返回 OK 。当 key 不是哈希表(hash)类型时，返回一个错误。
     */
    public String hashMultipleSet(final int dbIndex, final String key, final Map<String, String> hash, final int expire) {
        return new Executor<String>(jedisPool, dbIndex) {
            @Override
            String execute() {
                Pipeline pipeline = jedis.pipelined();
                Response<String> result = pipeline.hmset(key, hash);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。 时间复杂度: O(N) (N为fields的数量)
     *
     * @param key key
     * @param fields field的数组
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    public List<String> hashMultipleGet(final int dbIndex, final String key, final String... fields) {
        return new Executor<List<String>>(jedisPool, dbIndex) {
            @Override
            List<String> execute() {
                return jedis.hmget(key, fields);
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 同时设置这个 key 的生存时间
     *
     * @param key key
     * @param fields field的数组
     * @param expire 生命周期，单位为秒
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    public List<String> hashMultipleGet(final int dbIndex, final String key, final int expire, final String... fields) {
        return new Executor<List<String>>(jedisPool, dbIndex) {
            @Override
            List<String> execute() {
                Pipeline pipeline = jedis.pipelined();
                Response<List<String>> result = pipeline.hmget(key, fields);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleSet(String, Map)}，在管道中执行
     *
     * @param pairs 多个hash的多个field
     * @return 操作状态的集合
     */
    public List<Object> batchHashMultipleSet(final int dbIndex, final List<Pair<String, Map<String, String>>> pairs) {
        return new Executor<List<Object>>(jedisPool, dbIndex) {
            @Override
            List<Object> execute() {
                Pipeline pipeline = jedis.pipelined();
                for (Pair<String, Map<String, String>> pair : pairs) {
                    pipeline.hmset(pair.getKey(), pair.getValue());
                }
                return pipeline.syncAndReturnAll();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleSet(String, Map)}，在管道中执行
     *
     * @param data Map<String, Map<String, String>>格式的数据
     * @return 操作状态的集合
     */
    public List<Object> batchHashMultipleSet(final int dbIndex, final Map<String, Map<String, String>> data) {
        return new Executor<List<Object>>(jedisPool, dbIndex) {
            @Override
            List<Object> execute() {
                Pipeline pipeline = jedis.pipelined();
                for (Map.Entry<String, Map<String, String>> iter : data.entrySet()) {
                    pipeline.hmset(iter.getKey(), iter.getValue());
                }
                return pipeline.syncAndReturnAll();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleGet(String, String...)}，在管道中执行
     *
     * @param pairs 多个hash的多个field
     * @return 执行结果的集合
     */
    public List<List<String>> batchHashMultipleGet(final int dbIndex, final List<Pair<String, String[]>> pairs) {
        return new Executor<List<List<String>>>(jedisPool, dbIndex) {
            @Override
            List<List<String>> execute() {
                Pipeline pipeline = jedis.pipelined();
                List<List<String>> result = new ArrayList<List<String>>(pairs.size());
                List<Response<List<String>>> responses = new ArrayList<Response<List<String>>>(
                    pairs.size());
                for (Pair<String, String[]> pair : pairs) {
                    responses.add(pipeline.hmget(pair.getKey(), pair.getValue()));
                }
                pipeline.sync();
                for (Response<List<String>> resp : responses) {
                    result.add(resp.get());
                }
                return result;
            }
        }.getResult();

    }

    /**
     * 返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。 时间复杂度: O(N)
     *
     * @param key key
     * @return 以列表形式返回哈希表的域和域的值。若 key 不存在，返回空列表。
     */
    public Map<String, String> hashGetAll(final int dbIndex, final String key) {
        return new Executor<Map<String, String>>(jedisPool, dbIndex) {

            @Override
            Map<String, String> execute() {
                return jedis.hgetAll(key);
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。 同时设置这个 key 的生存时间
     *
     * @param key key
     * @param expire 生命周期，单位为秒
     * @return 以列表形式返回哈希表的域和域的值。若 key 不存在，返回空列表。
     */
    public Map<String, String> hashGetAll(final int dbIndex, final String key, final int expire) {
        return new Executor<Map<String, String>>(jedisPool, dbIndex) {
            @Override
            Map<String, String> execute() {
                Pipeline pipeline = jedis.pipelined();
                Response<Map<String, String>> result = pipeline.hgetAll(key);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 获取hash的field数量
     */
    public Long hashLen(final int dbIndex, final String key) {
        return new Executor<Long>(jedisPool, dbIndex) {
            @Override
            Long execute() {
                return jedis.hlen(key);
            }
        }.getResult();
    }

    /**
     * 获取hash的field数量
     */
    public Long hashLen(final int dbIndex, final String key, final int expire) {
        return new Executor<Long>(jedisPool, dbIndex) {
            @Override
            Long execute() {
                Pipeline pipeline = jedis.pipelined();
                Response<Long> result = pipeline.hlen(key);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 返回 hash 的所有 field
     */
    public Set<String> hashKeys(final int dbIndex, final String key) {
        return new Executor<Set<String>>(jedisPool, dbIndex) {
            @Override
            Set<String> execute() {
                return jedis.hkeys(key);
            }
        }.getResult();
    }

    /**
     * 返回 hash 的所有 field
     */
    public Set<String> hashKeys(final int dbIndex, final String key, final int expire) {
        return new Executor<Set<String>>(jedisPool, dbIndex) {

            @Override
            Set<String> execute() {
                Pipeline pipeline = jedis.pipelined();
                Response<Set<String>> result = pipeline.hkeys(key);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashGetAll(String)}
     *
     * @param keys key的数组
     * @return 执行结果的集合
     */
    public List<Map<String, String>> batchHashGetAll(final int dbIndex, final String... keys) {
        return new Executor<List<Map<String, String>>>(jedisPool, dbIndex) {
            @Override
            List<Map<String, String>> execute() {
                Pipeline pipeline = jedis.pipelined();
                List<Map<String, String>> result = new ArrayList<Map<String, String>>(keys.length);
                List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(
                    keys.length);
                for (String key : keys) {
                    responses.add(pipeline.hgetAll(key));
                }
                pipeline.sync();
                for (Response<Map<String, String>> resp : responses) {
                    result.add(resp.get());
                }
                return result;
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleGet(String, String...)}，
     * 与{@link #batchHashGetAll(String...)}不同的是，返回值为Map类型
     *
     * @param keys key的数组
     * @return 多个hash的所有filed和value
     */
    public Map<String, Map<String, String>> batchHashGetAllForMap(final int dbIndex, final String... keys) {
        return new Executor<Map<String, Map<String, String>>>(jedisPool, dbIndex) {

            @Override
            Map<String, Map<String, String>> execute() {
                Pipeline pipeline = jedis.pipelined();
                // 设置map容量防止rehash
                int capacity = 1;
                while ((int) (capacity * 0.75) <= keys.length) {
                    capacity <<= 1;
                }

                Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>(capacity);

                List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(keys.length);

                for (String key : keys) {
                    responses.add(pipeline.hgetAll(key));
                }

                pipeline.sync();
                for (int i = 0; i < keys.length; ++i) {
                    result.put(keys[i], responses.get(i).get());
                }
                return result;
            }
        }.getResult();
    }

}
