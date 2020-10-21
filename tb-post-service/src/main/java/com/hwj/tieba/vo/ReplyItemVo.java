package com.hwj.tieba.vo;

import com.hwj.tieba.entity.Reply;

public class ReplyItemVo {
    private Reply reply;
    private AccountVo accountVO;

    public ReplyItemVo(Reply reply) {
        this.reply = reply;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public AccountVo getAccountVO() {
        return accountVO;
    }

    public void setAccountVO(AccountVo accountVO) {
        this.accountVO = accountVO;
    }
}
