package com.hwj.tieba.config.common;

public class Constants {
    private Constants(){}
    /**登录token的key */
    public static final String LOGIN_NAME_KEY = "LoginNameKey";
    /**登录token*/
    public static final String LOGIN_TOKEN = "LoginToken";

    /***redis 帖子相关的key以这个打头**/
    public static final String POST_TOKEN_PREFIX = "TB:POST:";

    /**redis account相关的key以这个打头*/
    public static final String ACCOUNT_TOKEN_PREFIX = "TB:ACCOUNT:";

    public static final String SPRING_SESSION_ID_TOKEN_PREFIX  = "spring:session:sessions:";

}
