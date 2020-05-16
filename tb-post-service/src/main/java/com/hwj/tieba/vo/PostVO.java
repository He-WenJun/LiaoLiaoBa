package com.hwj.tieba.vo;
import com.hwj.tieba.entity.Post;
import java.util.Date;

public class PostVO {
    private String postId;
    private String postTypeName;
    private String topic;
    private String userId;
    private String baId;
    private String postName;
    private String content;
    private Long commentCount;
    private Long readCount;
    private String latestCommentDate;
    private Date enrollDate;
    private Date updateDate;

    public PostVO(Post post){
        this.postId = post.getPostId();
        this.userId = post.getUserId();
        this.baId = post.getBaId();
        if(post.getPostName().length() > 41){
            this.postName = post.getPostName().substring(0,41)+"...";
        }else {
            this.postName = post.getPostName();
        }
        this.content = post.getContent();
        this.readCount = post.getReadCount();
        this.enrollDate = post.getEnrollDate();
        this.updateDate = post.getUpdateDate();
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTypeName() {
        return postTypeName;
    }

    public void setPostTypeName(String postTypeName) {
        this.postTypeName = postTypeName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBaId() {
        return baId;
    }

    public void setBaId(String baId) {
        this.baId = baId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReadCount() {
        return readCount;
    }

    public void setReadCount(Long readCount) {
        this.readCount = readCount;
    }

    public String getLatestCommentDate() {
        return latestCommentDate;
    }

    public void setLatestCommentDate(String latestCommentDate) {
        this.latestCommentDate = latestCommentDate;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}
