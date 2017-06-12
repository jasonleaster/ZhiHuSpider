package org.jasonleaster.spiderz.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.jasonleaster.spiderz.dao.IUserRelationshipDAO;
import org.jasonleaster.spiderz.model.UserRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: jasonleaster
 * Date  : 2017/6/2
 * Email : jasonleaster@gmail.com
 * Description:
 * 用户关系服务类，屏蔽上层对底层DAO的访问
 */
@Service("userRelationshipService")
public class UserRelationshipService {

    private static final Logger log = Logger.getLogger(UserRelationshipService.class);

    @Autowired
    private IUserRelationshipDAO userRelationshipDAO;

    public UserRelationshipService() {
    }

    public int saveUserRelationships(List<UserRelationship> relationships) {

        if (relationships != null && !relationships.isEmpty()) {
            return userRelationshipDAO.insertUsersRelationships(relationships);
        } else {
            log.info("Attention! ##UserRelationshipService.saveUserRelationships##"
                + "The list which you are trying to insert into database is empty!");
            return -1;
        }
    }

    public List<String> filterExistedStartNode(List<String> toUrlTokens) {
        if (toUrlTokens != null && !toUrlTokens.isEmpty()) {
            return userRelationshipDAO.filterExistedRelationshipStartNode(toUrlTokens);
        } else {
            log.info("Attention! ##UserRelationshipService.filterExistedStartNode## "
                + "The parameter is empty ");
            return null;
        }
    }
}
