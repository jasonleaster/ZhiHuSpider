package org.jasonleaster.spiderz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.UnsupportedEncodingException;

/**
 * Author: jasonleaster
 * Date  : 2017/5/27
 * Email : jasonleaster@gmail.com
 * Description:
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserRelationship {

    private String fromUrlToken;

    private String fromUserId;

    @JsonProperty("url_token")
    private String toUrlToken;

    @JsonProperty("id")
    private String toUserId;

    @JsonProperty("is_followed")
    private boolean isFollowed;

    @JsonProperty("is_following")
    private boolean isFollowing;

    @JsonProperty("avatar_url_template")
    private String avatarUrlTemplate;

    @JsonProperty("user_type")
    private String userType;

    @JsonProperty("answer_count")
    private Integer answerCount;

    @JsonProperty("type")
    private String type;

    @JsonProperty("articles_count")
    private Integer articlesCount;

    @JsonProperty("name")
    private String name;

    @JsonProperty("is_advertiser")
    private boolean isAdvertiser;

    @JsonProperty("headline")
    private String headline;

    @JsonProperty("gender")
    private Integer gender;

    @JsonProperty("url")
    private String url;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("is_org")
    private boolean isOrg;

    @JsonProperty("follower_count")
    private Integer followerCount;

    public String getFromUrlToken() {
        return fromUrlToken;
    }

    public void setFromUrlToken(String fromUrlToken) {
        this.fromUrlToken = fromUrlToken;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUrlToken() {
        return toUrlToken;
    }

    public void setToUrlToken(String toUrlToken) {
        this.toUrlToken = toUrlToken;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public boolean getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(boolean followed) {
        isFollowed = followed;
    }

    public String getAvatarUrlTemplate() {
        return avatarUrlTemplate;
    }

    public void setAvatarUrlTemplate(String avatarUrlTemplate) {
        this.avatarUrlTemplate = avatarUrlTemplate;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    public boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        isFollowing = isFollowing;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getArticlesCount() {
        return articlesCount;
    }

    public void setArticlesCount(Integer articlesCount) {
        this.articlesCount = articlesCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        try{
            this.name = new String(name.getBytes(), "UTF-8");
        }catch (UnsupportedEncodingException e) {
            this.name = name;
        }
    }

    public boolean getIsAdvertiser() {
        return isAdvertiser;
    }

    public void setIsAdvertiser(boolean isAdvertiser) {
        this.isAdvertiser = isAdvertiser;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean getIsOrg() {
        return isOrg;
    }

    public void setIsOrg(boolean isOrg) {
        this.isOrg = isOrg;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }
}
