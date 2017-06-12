package org.jasonleaster.spiderz.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.jasonleaster.spiderz.model.UserProfileInfo;
import org.springframework.stereotype.Repository;

/**
 * Author: jasonleaster
 * Date  : 2017/5/27
 * Email : jasonleaster@gmail.com
 * Description:
 */
@Repository("userProfileDAO")
public interface IUserProfileDAO {

    /**
     * 批量插入用户的个人信息
     * @param usersProfileInfo 用户个人信息列表
     * @return 影响数据库的行数
     */
    int insertUsersProfileInfo(@Param("profiles") List<UserProfileInfo> usersProfileInfo);

    /**
     *  查询用户列表信息
     * @return 用户列表
     */
    List<UserProfileInfo> queryUsersProfileInfo();
}
