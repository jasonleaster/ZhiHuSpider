package org.jasonleaster.spiderz.processor;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jasonleaster.spiderz.constants.RedisConstants;
import org.jasonleaster.spiderz.model.PageInfo;
import org.jasonleaster.spiderz.model.RelationShipPaginationModel;
import org.jasonleaster.spiderz.model.UserRelationship;
import org.jasonleaster.spiderz.service.UserProfileInfoService;
import org.jasonleaster.spiderz.service.UserRelationshipService;
import org.jasonleaster.spiderz.url.UrlFactory;
import org.jasonleaster.spiderz.utils.JedisPoolUtils;
import org.jasonleaster.spiderz.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

/**
 * Author: jasonleaster
 * Date  : 2017/5/27
 * Email : jasonleaster@gmail.com
 * Description:
 */
@Repository("zhihuUserFollowersProcessor")
public class ZhihuUserFollowersProcessor implements PageProcessor {

    private static final Logger log = Logger.getLogger(ZhihuUserFollowersProcessor.class);

    /**
     * 队列的最大长度
     */
    private static final int limitedQueueLen = 1000;

    /**
     * 批量插入数据的规模
     */
    private static final int BATCH_SIZE_THRESHOLD = 50;

    /**
     * 缓存待插入DAO的用户关系
     */
    private List<UserRelationship> userRelationships = new ArrayList<>(BATCH_SIZE_THRESHOLD);

    @Autowired
    private UserRelationshipService userRelationshipService;

    @Autowired
    private UserProfileInfoService userProfileInfoService;

    private Site site = Site.me()
        .setUserAgent(
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36\n")
        .addHeader("accept", "application/json, text/plain, */*")
        .addHeader("Accept-charset", "UTF-8")
        .addHeader("Accept-Encoding", "gzip, deflate, sdch, br")
        .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
        .addHeader("Cache-Control", "no-cache")
        .addHeader("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20")
        .addHeader("Host", "www.zhihu.com")
        .addHeader("Pragma", "no-cache")
        .addHeader("x-udid", "AIBCvvXSzguPTuq9ctFwIhT0Wy9XppYISL8=");

    public ZhihuUserFollowersProcessor() {
    }

    /**
     * 步骤:
     * 1. 从本页面的Url中提取出urlToken，即被关注者
     * 2. 获取返回的Json数据并反序列化为模型对象
     * 3. 根据分页查询的信息判断当前页面是否是第一页，
     * 如果是，则把该被关注用户的follower的剩下页面的
     * 分页查询Url都写入到Redis缓存队列中
     * 4. 滤掉
     * A.获取follower列表中有哪些是已经爬过的，对于已经爬过的用户不再重复加入到待爬取队列里面
     * B.无效用户(例如连urlToken都没有的“僵尸用户”)
     * 5. 关系数据写入Buffer，如果Buffer已满，则把Buffer的数据入库。
     * 6. 把过滤后followers的 urlToken写入到待爬取队列中
     */
    @Override
    public void process(Page page) {

        String url = page.getUrl().get();
        String fromUrlToken = UrlFactory.getTokenFromUrl(url);

        Json json = page.getJson();

        RelationShipPaginationModel paginationModel = (RelationShipPaginationModel)
            JsonUtil.toObject(json.get(), RelationShipPaginationModel.class);

        if (paginationModel == null) {
            log.info("##ZhihuUserFollowersProcessor.process##  "
                + "paginationModel is NULL!"
                + "fromUrlToken: " + fromUrlToken);
            return;
        }

        PageInfo pageInfo = paginationModel.getPaging();
        if (pageInfo.getIsStart()) {
            addAllRestPaginationUrlsIntoQueue(fromUrlToken, pageInfo);
        }

        List<UserRelationship> relationships = paginationModel.getData();

        if (relationships == null || relationships.isEmpty()) {
            return;
        }

        // 过滤不必要的数据, 并对现有数据序列化
        List<String> toUrlTokens = new ArrayList<>();
        relationships.forEach(relationship -> toUrlTokens.add(relationship.getToUrlToken()));

        List<String> duplicatedStartNodes = userRelationshipService
            .filterExistedStartNode(toUrlTokens);

        List<String> serializedObjects     = new ArrayList<>();
        List<UserRelationship> toBeRemoved = new ArrayList<>();
        for (UserRelationship relationship : relationships) {
            if (isToBeRemoved(duplicatedStartNodes, relationship)){
                toBeRemoved.add(relationship);
            } else {
                /*
                 * 由于分页查询返回的结果模型中没有来源用户，
                 * 因此需要设置该关系的来源(隶属于哪个用户)
                 */
                relationship.setFromUrlToken(fromUrlToken);

                // 由于关系网的存在环状结构，为避免重复抓取关系数据并且避免进入环状路径
                // 只把没有重复的Start Node加入消息队列中
                serializedObjects.add(relationship.getToUrlToken());
            }
        }

        relationships.removeAll(toBeRemoved);

        storeUserProfileInfo(relationships);

        addMessageIntoUrlTokenQueue(serializedObjects);
    }

    @Override
    public Site getSite() {
        return site;
    }

    private synchronized void storeUserProfileInfo(List<UserRelationship> relationships) {
        if (userRelationships.size() >= BATCH_SIZE_THRESHOLD) {
            userRelationshipService.saveUserRelationships(userRelationships);
            userRelationships.clear();
        }

        userRelationships.addAll(relationships);
    }

    /**
     * 把用户被关注列表的分页查询的Url加入到分页查询队列
     *
     * @param pageInfo 某一页的分页信息
     */
    private synchronized void addAllRestPaginationUrlsIntoQueue(String fromUrlToken,
        PageInfo pageInfo) {

        List<String> urls = new ArrayList<>();

        int totalFollowings = pageInfo.getTotals();
        int limit = 20;// 每页最多展示20个数据项
        for (int offset = 20; offset < totalFollowings; offset += limit) {
            String paginationUrl = UrlFactory.getRestfulFollowerAPI(fromUrlToken, offset, limit);
            urls.add(paginationUrl);
        }

        JedisPoolUtils.getInstance().addMessagesIntoQueue(
            RedisConstants.dbIndexOfPaginationUrls,
            RedisConstants.messageQueueNameForPaginationUrls,
            limitedQueueLen, urls);
    }

    /**
     * @param serializedObjects 单独的一个synchronized block 保证对于客户端来说整个过程
     * 只有一个线程往消息队列里面加数据， 避免竞争情况的出现
     */
    private synchronized void addMessageIntoUrlTokenQueue(List<String> serializedObjects) {

        long queueLen = JedisPoolUtils.getInstance().getListLen(
            RedisConstants.dbIndexOfUrlTokens,
            RedisConstants.urlTokenQueueForUserProfile);

        int expectedToAddLen = serializedObjects.size();
        if (queueLen + expectedToAddLen > limitedQueueLen) {
            // 功能未实现之前暂时直接丢弃
            // TODO 不能塞下的数据写入数据库直接入库
        } else {
            // 将urlTokens分别放入两个不同的队列
            JedisPoolUtils.getInstance().addMessagesIntoQueue(
                RedisConstants.dbIndexOfUrlTokens,
                RedisConstants.urlTokenQueueForUserProfile,
                limitedQueueLen, serializedObjects);
        }

        queueLen = JedisPoolUtils.getInstance().getListLen(
            RedisConstants.dbIndexOfUrlTokens,
            RedisConstants.urlTokenQueueForFollowerPagination);

        if (queueLen + expectedToAddLen > limitedQueueLen){
            // 功能未实现之前暂时直接丢弃
            // TODO 不能塞下的数据写入数据库直接入库
        }else{
            JedisPoolUtils.getInstance().addMessagesIntoQueue(
                RedisConstants.dbIndexOfUrlTokens,
                RedisConstants.urlTokenQueueForFollowerPagination,
                limitedQueueLen, serializedObjects);
        }
    }

    /**
     * @param duplicatedStartNode 重复的urlTokens
     * @param userRelationship 某一关系数据
     * @return 是否符合加入删除链表的条件，如果符合返回True，否则返回False
     */
    private boolean isToBeRemoved(List<String> duplicatedStartNode, UserRelationship userRelationship){
        if (userRelationship.getToUrlToken() == null ||
            userRelationship.getToUrlToken().isEmpty()){
            return true;
        }

        if (duplicatedStartNode != null && !duplicatedStartNode.isEmpty() &&
                duplicatedStartNode.contains(userRelationship)){
            return true;
        }

        return false;
    }
}
