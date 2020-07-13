package com.hwj.tieba.vo;

import com.hwj.tieba.entity.Comment;
import com.hwj.tieba.entity.File;

import java.util.List;

public class CommentItemVo {
    private Comment comment;
    private AccountVo accountVO;
    private List<ReplyItemVo> replyVOList;
    private List<File> fileList;

    public CommentItemVo(){}

    public CommentItemVo(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public AccountVo getAccountVO() {
        return accountVO;
    }

    public void setAccountVO(AccountVo accountVO) {
        this.accountVO = accountVO;
    }

    public List<ReplyItemVo> getReply() {
        return replyVOList;
    }

    public void setReply(List<ReplyItemVo> reply) {
        this.replyVOList = reply;
    }

    public List<ReplyItemVo> getReplyVOList() {
        return replyVOList;
    }

    public void setReplyVOList(List<ReplyItemVo> replyVOList) {
        this.replyVOList = replyVOList;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        for(int i = 0; i < fileList.size(); i++){
            fileList.get(i).setSrc(fileList.get(i).getSrc().substring(fileList.get(i).getSrc().indexOf("file")-1));
        }
        this.fileList = fileList;
    }
}
