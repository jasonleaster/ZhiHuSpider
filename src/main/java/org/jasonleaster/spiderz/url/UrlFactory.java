package org.jasonleaster.spiderz.url;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringUtils;

/**
 * Author: jasonleaster
 * Date  : 2017/5/24
 * Email : jasonleaster@gmail.com
 * Description:
 */
public class UrlFactory {

    private static final String restfulUrlCommonPart = "https://www.zhihu.com/api/v4/members/";

    private static final String restfulUserProfileAttributesUrl = "?include=locations%2Cemployments%2Cgender%2Ceducations%2Cbusiness%2Cvoteup_count%2Cthanked_Count%2Cfollower_count%2Cfollowing_count%2Ccover_url%2Cfollowing_topic_count%2Cfollowing_question_count%2Cfollowing_favlists_count%2Cfollowing_columns_count%2Cavatar_hue%2Canswer_count%2Carticles_count%2Cpins_count%2Cquestion_count%2Ccolumns_count%2Ccommercial_question_count%2Cfavorite_count%2Cfavorited_count%2Clogs_count%2Cmarked_answers_count%2Cmarked_answers_text%2Cmessage_thread_token%2Caccount_status%2Cis_active%2Cis_force_renamed%2Cis_bind_sina%2Csina_weibo_url%2Csina_weibo_name%2Cshow_sina_weibo%2Cis_blocking%2Cis_blocked%2Cis_following%2Cis_followed%2Cmutual_followees_count%2Cvote_to_count%2Cvote_from_count%2Cthank_to_count%2Cthank_from_count%2Cthanked_count%2Cdescription%2Chosted_live_count%2Cparticipated_live_count%2Callow_message%2Cindustry_category%2Corg_name%2Corg_homepage%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics";

    /**
     * 分页查询接口
     * offset: 0
     * limit : 20 此处20为上限值
     */

    private static final String paginationOffset = "&offset=";
    private static final String paginationLimit  = "&limit=";
    private static final String restfulFollowerAttributesUrl = "/followers?include=data%5B*%5D.answer_count%2Carticles_count%2Cgender%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics";


    public static String getRestfulUserProfileAPI(String urlToken){
        return restfulUrlCommonPart + urlToken + restfulUserProfileAttributesUrl;
    }

    public static String getRestfulFollowerAPI(String urlToken, int offset, int limit){
        return  restfulUrlCommonPart + urlToken +
                restfulFollowerAttributesUrl +
                paginationOffset + offset +
                paginationLimit  + limit;
    }

    /**
     * 根据两个URL的特点，提取其中的urlToken
     * @param url
     * @return
     */
    public static String getTokenFromUrl(String url){
        int start = "https://www.zhihu.com/api/v4/members/".length();

        int end   =  url.indexOf("/followers?");
        end = end > 0 ? end : url.indexOf('?');
        return url.substring(start, end);
    }
}