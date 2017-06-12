package org.jasonleaster.spiderz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jasonleaster.spiderz.utils.JsonUtil;

/**
 * Author: jasonleaster
 * Date  : 2017/5/25
 * Email : jasonleaster@gmail.com
 * Description: Profile Information of Users
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserProfileInfo {

    /**
     * 用户的hash Id值 如："2528c0d822893dd5112b57b813ed47e7"
     */
    @JsonProperty("id")
    private String userId;

    /**
     * URL使用的查询参数(一般就是用户名)
     */
    @JsonProperty("url_token")
    private String urlToken;

    /**
     * 用户名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 性别
     */
    @JsonProperty("gender")
    private Integer gender;

    /**
     * 标题栏个性签名
     */
    @JsonProperty("headline")
    private String headline;

    /**
     * 该用户的回答被赞同的次数
     */
    @JsonProperty("voteup_count")
    private Long voteUpCount;

    /**
     * 答案被收藏的次数
     */
    @JsonProperty("favorited_count")
    private Long favoritedCount;

    /**
     * ta被多少人关注
     */
    @JsonProperty("follower_count")
    private Long followerCount;

    /**
     * ta关注的人数
     */
    @JsonProperty("following_count")
    private Integer followingCount;

    /**
     * 关注的专栏数目
     */
    @JsonProperty("following_columns_count")
    private Integer followingColumnsCount;


    /**
     * 提供答案的数目
     */
    @JsonProperty("answer_count")
    private Integer answerCount;

    /**
     * 文章数目
     */
    @JsonProperty("articles_count")
    private Integer articlesCount;

    /**
     * 提问数目
     */
    @JsonProperty("question_count")
    private Integer questionCount;

    /**
     * 参与公共编辑的次数
     */
    @JsonProperty("logs_count")
    private Integer logsCount;


    /**
     * 关注问题的数目
     */
    @JsonProperty("following_question_count")
    private Integer followingQuestionCount;

    /**
     * 送出感谢的数目
     */
    @JsonProperty("thanked_count")
    private Integer thankedCount;


    /**
     * 个人描述
     */
    @JsonProperty("description")
    private String description;

//    /**
//     * 是否关联微博
//     */
//    private Boolean is_bind_sina;
//
//    /**
//     * 是否关联该用户
//     */
//    private Boolean is_following;
//
//    /**
//     * 主持过Live的次数
//     */
//    private Integer hosted_live_count;
//    /**
//     * 参加Live的次数
//     */
//    private Integer participated_live_count;

//    /**
//     * 他的历史雇主们
//     */
//    private List<Employment> employments;
//
//    /**
//     * 关注话题的数目
//     */
//    private Integer following_topic_count;

//
//    // optional attributes
//    private List<EducationInfo> educations;


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        UserProfileInfo othersProfileInfo = (UserProfileInfo) obj;
        return  this.userId.equals(othersProfileInfo.userId);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        //TODO
        return super.clone();
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrlToken() {
        return urlToken;
    }

    public void setUrlToken(String urlToken) {
        this.urlToken = urlToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Long getVoteUpCount() {
        return voteUpCount;
    }

    public void setVoteUpCount(Long voteUpCount) {
        this.voteUpCount = voteUpCount;
    }

    public Long getFavoritedCount() {
        return favoritedCount;
    }

    public void setFavoritedCount(Long favoritedCount) {
        this.favoritedCount = favoritedCount;
    }

    public Long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getFollowingColumnsCount() {
        return followingColumnsCount;
    }

    public void setFollowingColumnsCount(Integer followingColumnsCount) {
        this.followingColumnsCount = followingColumnsCount;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    public Integer getArticlesCount() {
        return articlesCount;
    }

    public void setArticlesCount(Integer articlesCount) {
        this.articlesCount = articlesCount;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getLogsCount() {
        return logsCount;
    }

    public void setLogsCount(Integer logsCount) {
        this.logsCount = logsCount;
    }

    public Integer getFollowingQuestionCount() {
        return followingQuestionCount;
    }

    public void setFollowingQuestionCount(Integer followingQuestionCount) {
        this.followingQuestionCount = followingQuestionCount;
    }

    public Integer getThankedCount() {
        return thankedCount;
    }

    public void setThankedCount(Integer thankedCount) {
        this.thankedCount = thankedCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
