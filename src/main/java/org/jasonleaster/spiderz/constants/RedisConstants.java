package org.jasonleaster.spiderz.constants;

/**
 * Author: jasonleaster
 * Date  : 2017/5/27
 * Email : jasonleaster@gmail.com
 * Description:
 */
public class RedisConstants {

    private static final int dbIndex_1 = 1;

    private static final int dbIndex_2 = 2;

    private static final int dbIndex_3 = 3;

    /**
     * Redis Key的名字
     */
    public static final String urlTokenQueueForFollowerPagination = "urlTokenQueueForFollowerPagination";

    public static final String urlTokenQueueForUserProfile = "urlTokenQueueForUserProfile";

    public static final int dbIndexOfUrlTokens = dbIndex_1;

    public static final String messageQueueNameForPaginationUrls = "messageQueueForPaginationUrls";

    public static final int dbIndexOfPaginationUrls = dbIndex_2;

}
