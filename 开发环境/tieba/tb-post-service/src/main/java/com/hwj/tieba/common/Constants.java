package com.hwj.tieba.common;

public class Constants {

    /**redis当中的key过期时间*/
    public static final Integer KEY_EXPIRES = 10;

    /***redis 帖子相关的key以这个打头**/
    public static final String POST_TOKEN_PREFIX = "TB:POST:";

    /**redis account相关的key以这个打头*/
    public static final String ACCOUNT_TOKEN_PREFIX = "TB:ACCOUNT:";

    /**吧的等级每级所需经验值*/
    public static final Integer LEVEL = 1000;

    /**在贴吧签到时贴吧所加的经验值*/
    public static final Integer SING_IN_EXP = 10;

    /**评论时贴吧所加的经验值*/
    public static final Integer COMMENT_EXP = 20;

    /**订阅类型*/
    public interface SubscribeType{
        /**用户*/
        int USER = 1;
        /**模块*/
        int MODULE = 2;
    }

    /**生效状态*/
    public interface StateType{
        /**生效*/
        int TAKE_EFFECT = 1;
        /**失效*/
        int LOSE_EFFICACY = 2;
    }

    public interface MessageType{
        int REPLY = 1;
        int DELETE_POST = 2;
        int COMMENT = 3;
    }

    /**角色ID*/
    public interface RoleType{
        /**普通用户*/
        int ORDINARY = 1;
        /**小吧管理员*/
        int HELPER = 4;
        /**小吧主*/
        int MASTER  = 3;
        /**系统管理员*/
        int ADMIN = 2;
    }

    /**分页每页显示条数*/
    public static final Integer pageCountSize = 10;

}
