package org.jasonleaster.spiderz;

import org.jasonleaster.spiderz.constants.RedisConstants;
import org.jasonleaster.spiderz.url.UrlFactory;
import org.jasonleaster.spiderz.utils.JedisPoolUtils;
import org.jasonleaster.spiderz.utils.SpringContextUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant.Method;

/**
 * Author: jasonleaster
 * Date  : 2017/6/10
 * Email : jasonleaster@gmail.com
 * Description:
 */
public class UserProfileCrawler {

    private static final int defaultThreadForCrawler = 10;

    public static void main(String[] args) {
        //初始化Spring的上下文
        SpringContextUtils.getContext();

        PageProcessor userProfileProcessor = (PageProcessor)
            SpringContextUtils.getBean("zhihuUserProfileProcessor");

        Spider spiderForUserProfile = Spider.create(userProfileProcessor);
        for (; true; ) {

            int threadNum = 0;
            for (; threadNum < defaultThreadForCrawler; threadNum++) {

                // 从消息队列拉取数据
                String urlToken = JedisPoolUtils.getInstance().getMessageFromQueue(
                    RedisConstants.dbIndexOfUrlTokens,
                    RedisConstants.urlTokenQueueForUserProfile);

                if (urlToken != null) {
                    String url = UrlFactory.getRestfulUserProfileAPI(urlToken);
                    spiderForUserProfile.addRequest(requestGETBuilder(url));
                } else {
                    break;
                }
            }

            if (threadNum == 0){
                Thread.yield();
            }else{
                spiderForUserProfile.thread(threadNum).run();
            }
        }
    }

    private static Request requestGETBuilder(String url){
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setUrl(url);
        return request;
    }
}
