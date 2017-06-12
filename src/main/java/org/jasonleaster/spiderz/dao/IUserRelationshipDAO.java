package org.jasonleaster.spiderz.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.jasonleaster.spiderz.model.UserRelationship;
import org.springframework.stereotype.Repository;

/**
 * Author: jasonleaster
 * Date  : 2017/6/2
 * Email : jasonleaster@gmail.com
 * Description:
 */
@Repository("userRelationshipDAO")
public interface IUserRelationshipDAO {
    /**
     * 批量插入用户的关系列表
     * @param userRelationships 用户关系列表
     * @return 影响数据库的行数
     */
    int insertUsersRelationships(@Param("userRelationships") List<UserRelationship> userRelationships);

    /**
     * 从toUrlTokens 中过滤已经存在于 fromUrlToken 字段的关系，检测环路节点
     * @param toUrlTokens
     * @return
     */
    List<String> filterExistedRelationshipStartNode(@Param("toUrlTokens") List<String> toUrlTokens);
}
