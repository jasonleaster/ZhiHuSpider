package org.jasonleaster.spiderz.processor;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jasonleaster.spiderz.model.UserProfileInfo;
import org.jasonleaster.spiderz.service.UserProfileInfoService;
import org.jasonleaster.spiderz.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

/**
 * Author: jasonleaster
 * Date  : 2017/5/24
 * Email : jasonleaster@gmail.com
 * Description:
 */
@Repository("zhihuUserProfileProcessor")
public class ZhihuUserProfileProcessor implements PageProcessor {

    private static final Logger log = Logger.getLogger(ZhihuUserProfileProcessor.class);

    /**
     * 队列的最大长度
     */
    private static final int limitedQueueLen = 1000;

    /**
     * 批量插入数据的规模
     */
    private static final int BATCH_SIZE = 2;

    /**
     * 缓存待插入DAO的个人用户详细信息
     */
    private List<UserProfileInfo> userProfileInfos = new ArrayList<>(1000);

    @Autowired
    private UserProfileInfoService userProfileInfoService;


    //.addHeader("Cookie", "q_c1=6163579a012c42aa905cbddf9fc56884|1495203426000|1495203426000; aliyungf_tc=AQAAAFJos3B4jQ0Asvl/DqYWcWiNbGfv; acw_tc=AQAAAApzFThGvg0Asvl/DuY58AkBHOBZ; l_n_c=1; _xsrf=d669717b2c5385c34ff1316da17b502a; d_c0=\"AIBCvvXSzguPTuq9ctFwIhT0Wy9XppYISL8=|1495638288\"; _zap=3a3e063a-2cf7-4aaf-8cf3-0094ef841fbe; __utma=155987696.45704276.1495640247.1495640247.1495640247.1; __utmc=155987696; __utmz=155987696.1495640247.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); r_cap_id=\"OWFmNmQ2ZjllOGI2NDdkMTkwY2Y0MTNmZTZhYzBlOGQ=|1495640581|39b37b728f318690339d5d71f634e1cc29f20c65\"; cap_id=\"NjQ2YTY3OTU0ZGRmNGViYTljODk5ZDBlMjllNTNiZDc=|1495640581|0690a86b22a427b65a1e3001a26f9d7402100813\"; l_cap_id=\"MTlkYjRkOTRkMjFiNGZlYzk0YmMyMzJkZDk5NjI0OTA=|1495640581|67bfbcb4c09f28e7329ecdc7d7fee4a2c0b82b46\"; capsion_ticket=\"2|1:0|10:1495640843|14:capsion_ticket|44:NjkxN2Y2YWMzNzBlNGUyM2EyMDAxZmRiOGQwZmRhYTI=|543ccad05d1e028c163b4ce8a69737aa3aaacd92d93fe4470631ccb3535c504d\"\n")

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
        .addHeader("Connection", "keep-alive")
        .addHeader("x-udid", "AIBCvvXSzguPTuq9ctFwIhT0Wy9XppYISL8=");

    public ZhihuUserProfileProcessor() {
    }

    @Override
    public void process(Page page) {
        Json json = page.getJson();

        UserProfileInfo userProfileInfo = (UserProfileInfo) JsonUtil
            .toObject(json.get(), UserProfileInfo.class);

        if (userProfileInfo == null) {
            log.info("userProfileInfo is Null");
            return;
        }

        storeUserProfileInfo(userProfileInfo);
    }

    @Override
    public Site getSite() {
        return site;
    }

    private synchronized void storeUserProfileInfo(UserProfileInfo userProfileInfo) {
        if (userProfileInfos.size() >= BATCH_SIZE) {
            userProfileInfoService.saveUsersProfileInfo(userProfileInfos);
            userProfileInfos.clear();
        }

        userProfileInfos.add(userProfileInfo);
    }
}