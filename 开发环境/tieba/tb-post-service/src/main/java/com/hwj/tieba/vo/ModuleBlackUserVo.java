package com.hwj.tieba.vo;

import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.ModuleBlackUser;

public class ModuleBlackUserVo {
    private AccountVo account;
    private ModuleBlackUser moduleBlackUser;

    public ModuleBlackUserVo(){}

    public ModuleBlackUserVo(AccountVo account, ModuleBlackUser moduleBlackUser) {
        this.account = account;
        this.moduleBlackUser = moduleBlackUser;
    }

    public AccountVo getAccount() {
        return account;
    }

    public void setAccount(AccountVo account) {
        this.account = account;
    }

    public ModuleBlackUser getModuleBlackUser() {
        return moduleBlackUser;
    }

    public void setModuleBlackUser(ModuleBlackUser moduleBlackUser) {
        this.moduleBlackUser = moduleBlackUser;
    }
}
