package org.jasonleaster.spiderz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Author: jasonleaster
 * Date  : 2017/5/27
 * Email : jasonleaster@gmail.com
 * Description:
 *  分页模型
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RelationShipPaginationModel {

    @JsonProperty("paging")
    private PageInfo paging;

    @JsonProperty("data")
    private List<UserRelationship> data;

    public PageInfo getPaging() {
        return paging;
    }

    public void setPaging(PageInfo paging) {
        this.paging = paging;
    }

    public List<UserRelationship> getData() {
        return data;
    }

    public void setData(List<UserRelationship> data) {
        this.data = data;
    }
}
