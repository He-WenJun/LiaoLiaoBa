package com.hwj.tieba.vo;

import com.hwj.tieba.entity.File;
import com.hwj.tieba.entity.Post;

import java.util.List;

public class PostItemVo {
    private PostVo postVo;
    private AccountVo accountVo;

    public PostItemVo(){}
    public PostItemVo(Post post, AccountVo accountVo) {
        this.postVo = new PostVo(post);
        this.accountVo = accountVo;
    }

    public AccountVo getAccountVo() {
        return accountVo;
    }

    public void setAccountVo(AccountVo accountVo) {
        this.accountVo = accountVo;
    }

    public PostVo getPostVo() {
        return postVo;
    }

    public void setPostVo(PostVo postVo) {
        this.postVo = postVo;
    }

}
