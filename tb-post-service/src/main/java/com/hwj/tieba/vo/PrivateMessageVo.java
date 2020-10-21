package com.hwj.tieba.vo;

import com.hwj.tieba.entity.PrivateMessage;

import java.util.List;

public class PrivateMessageVo {
    /**历史消息列表*/
    private List<PrivateMessage> privateMessageList;

    /**本列表的起始下标*/
    private Long index;

    /**是否是最后的历史消息*/
    private Boolean isLastMessage;

    public PrivateMessageVo(List<PrivateMessage> privateMessageList, Long index, Boolean isLastMessage) {
        this.privateMessageList = privateMessageList;
        this.index = index;
        this.isLastMessage = isLastMessage;
    }

    public List<PrivateMessage> getPrivateMessageList() {
        return privateMessageList;
    }

    public void setPrivateMessageList(List<PrivateMessage> privateMessageList) {
        this.privateMessageList = privateMessageList;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Boolean getLastMessage() {
        return isLastMessage;
    }

    public void setLastMessage(Boolean lastMessage) {
        isLastMessage = lastMessage;
    }
}
