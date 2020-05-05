package com.hwj.tieba.common;

public class Constants {

    /**redis当中的key过期时间*/
    public static final Integer KEY_EXPIRES = 60;

    /***redis 帖子相关的key以这个打头**/
    public static final String POST_TOKEN_PREFIX = "TB:POST:";

    /**redis account相关的key以这个打头*/
    public static final String ACCOUNT_TOKEN_PREFIX = "TB:ACCOUNT:";

    /**吧的等级每级所需经验值*/
    public static final Integer LEVEL = 1000;

    /**在贴吧签到时贴吧所加的经验值*/
    public static final Integer SING_IN_EXP = 10;

    /**订阅类型*/
    public interface SubscribeType{
        /**用户*/
        int USER = 1;
        /**吧*/
        int BA = 2;
    }

    /**分页每页显示条数*/
    public static final Integer pageCountSize = 5;

}
