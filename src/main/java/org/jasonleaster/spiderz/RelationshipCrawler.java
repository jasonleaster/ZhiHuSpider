package org.jasonleaster.spiderz;

import org.jasonleaster.spiderz.constants.RedisConstants;
import org.jasonleaster.spiderz.url.UrlFactory;
import org.jasonleaster.spiderz.utils.JedisPoolUtils;
import org.jasonleaster.spiderz.utils.SpringContextUtils;
import org.springframework.context.annotation.ComponentScan;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant.Method;

/**
 * Author: jasonleaster
 * Date  : 2017/5/27
 * Email : jasonleaster@gmail.com
 * Description:
 */

@ComponentScan
public class RelationshipCrawler {

    private static final int defaultThreadForCrawler = 3;

    public static void main(String[] args) {

        //初始化Spring的上下文
        SpringContextUtils.getContext();

        PageProcessor followersPageProcessor = (PageProcessor)
            SpringContextUtils.getBean("zhihuUserFollowersProcessor");

        Spider spiderForUserFollowing = Spider.create(followersPageProcessor);

        for (; true; ) {

            int threadNum = 0;
            for (; threadNum < defaultThreadForCrawler; threadNum++) {

                // 从消息队列拉取数据
                String paginationUrl = JedisPoolUtils.getInstance().getMessageFromQueue(
                    RedisConstants.dbIndexOfPaginationUrls,
                    RedisConstants.messageQueueNameForPaginationUrls);

                if (paginationUrl != null) {
                    spiderForUserFollowing.addRequest(requestGETBuilder(paginationUrl));
                } else {
                    // 当分页Url队列为空时，从用户Token队列获取新的用户构造查询Url
                    String urlToken = JedisPoolUtils.getInstance().getMessageFromQueue(
                        RedisConstants.dbIndexOfUrlTokens,
                        RedisConstants.urlTokenQueueForFollowerPagination);

                    if (urlToken == null) {
                        // 两个队列都为空的时候
                        break;
                    } else if (urlToken.isEmpty()) {
                        continue;
                    }

                    String url = UrlFactory.getRestfulFollowerAPI(urlToken, 0, 20);
                    Request request = requestGETBuilder(url);
                    spiderForUserFollowing.addRequest(request);

                    threadNum++;
                    break;
                }
            }

            if (threadNum == 0) {
                // 两个队列都为空的情况
                Thread.yield();
                continue;
            }

            /*
            * 跟普通的Runnable一样，阻塞式运行，会阻塞当前线程直至Spider运行结束。
            * 如果要异步调用，使用runAsync()
            * */
            spiderForUserFollowing.thread(threadNum).run();
        }
    }

    private static Request requestGETBuilder(String url) {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setUrl(url);
        return request;
    }
}
