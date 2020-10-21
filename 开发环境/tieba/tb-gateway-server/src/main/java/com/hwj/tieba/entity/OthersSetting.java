package com.hwj.tieba.entity;

//对其他用户的设置
public class OthersSetting {
    /*设置对应的账号Id*/
    private String userId;
    /*是否禁止他回复我的帖子*/
    private Boolean commentPost;
    /*是否拒绝接收他的私信*/
    private Boolean sendMessage;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean isCommentPost() {
        return commentPost;
    }

    public void setCommentPost(Boolean commentPost) {
        this.commentPost = commentPost;
    }

    public Boolean isSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(Boolean sendMessage) {
        this.sendMessage = sendMessage;
    }
}
