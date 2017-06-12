package org.jasonleaster.spiderz.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.jasonleaster.spiderz.dao.IUserProfileDAO;
import org.jasonleaster.spiderz.model.UserProfileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: jasonleaster
 * Date  : 2017/5/27
 * Email : jasonleaster@gmail.com
 * Description:
 */
@Service("userProfileInfoService")
public class UserProfileInfoService {

    private static final Logger log = Logger.getLogger(UserProfileInfoService.class);

    @Autowired
    private IUserProfileDAO userProfileDAO;

    public int saveUsersProfileInfo(List<UserProfileInfo> usersProfileInfos) {

        if (usersProfileInfos != null && !usersProfileInfos.isEmpty()) {
            return userProfileDAO.insertUsersProfileInfo(usersProfileInfos);
        }else {
            log.info("Attention! ##UserProfileInfoService.saveUsersProfileInfo##  "
                + "Parameter userProfileInfos is empty or null!");
            return -1;
        }
    }

}
