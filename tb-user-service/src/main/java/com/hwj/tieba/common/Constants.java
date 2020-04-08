package com.hwj.tieba.common;

import com.hwj.tieba.entity.Binding;

public class Constants {
    /**md5加密统一盐值*/
    public static final String MD5_SALT = "TIEBA";

    /**redis当中的key过期时间*/
    public static Integer KEY_EXPIRES = 60*30;

    /**账号状态类型*/
    public static interface StateType{
        /**封禁*/
        int STATE_BAN = 1;
        /**冻结*/
        int STATE_FREEZE = 2;
        /**禁言*/
        int STATE_BannedToPost  =3;
    }

    /**角色ID*/
    public static interface RoleType{
        /**普通用户*/
        int ORDINARY = 1;
        /**小吧管理员*/
        int HELPER = 4;
        /**小吧主*/
        int MASTER  = 3;
        /**系统管理员*/
        int ADMIN = 2;
    }

    /**绑定类型*/
    public static interface BindingType{
        /**邮箱绑定*/
        int MAIL = 1;
        /**手机绑定*/
        int PHONE = 2;
    }

    /***redis account相关的key以这个打头**/
    public static final String TOKEN_PREFIX = "TB:ACCOUNT:";
}
