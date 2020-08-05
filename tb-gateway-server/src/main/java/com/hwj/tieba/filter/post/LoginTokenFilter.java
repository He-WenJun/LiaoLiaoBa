package com.hwj.tieba.filter.post;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.config.common.Constants;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginTokenFilter extends ZuulFilter {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if(ctx.sendZuulResponse()){
            if("/api/user/verificationCode".equals(request.getRequestURI())){
                return false;
            }else if("/api/user/login".equals(request.getRequestURI())){
                return false;
            }else if("/api/user/enrollHold".equals(request.getRequestURI())){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Cookie[] cookies = request.getCookies();
        //若cookie的长度小于2则说明当前无账号登录，不做登录唯一性验证
        if(cookies!=null && cookies.length>=2){
            //包含redis储存token的key
            Cookie loginNameKeyCookie = null;
            //包含登录token
            Cookie loginTokenCookie = null;
            for (Cookie cookie:cookies){
                if(Constants.LOGIN_NAME_KEY.equals(cookie.getName())){
                    loginNameKeyCookie = cookie;
                }
                else if(Constants.LOGIN_TOKEN.equals(cookie.getName())){
                    loginTokenCookie = cookie;
                }
            }
            if(loginNameKeyCookie != null && loginTokenCookie != null){
                //获取redis中的token
                String correctLoginToken =null;
                try {
                    correctLoginToken = redisUtil.getStr(loginNameKeyCookie.getValue());
                }catch (Exception e){
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }


                //判断登录Token是否与redis中相同，若不同说明账号已在别处登录
                if(correctLoginToken.equals(loginTokenCookie.getValue())){
                    System.out.println("Token正确");
                    //对请求进行路由
                    ctx.setSendZuulResponse(true);
                }else{
                    System.out.println("Token错误");

                    String sessionId = ctx.getZuulRequestHeaders().get("sessionid");
                    //删除保存账号信息
                    redisUtil.hDel(sessionId,"Account");
                    //不对请求进行路由
                    ctx.setSendZuulResponse(false);

                    //让cookie中的登录token以及key过期
                    HttpServletResponse response = ctx.getResponse();
                    loginNameKeyCookie.setMaxAge(0);
                    loginNameKeyCookie.setValue(null);
                    loginNameKeyCookie.setPath("/api/");

                    loginTokenCookie.setMaxAge(0);
                    loginTokenCookie.setValue(null);
                    loginTokenCookie.setPath("/api/");

                    response.addCookie(loginNameKeyCookie);
                    response.addCookie(loginTokenCookie);

                    try {
                        ServerResponse<String> serverResponse = ServerResponse.createByErrorCodeMessage(3,"账号已在别处登录");
                        response.setHeader("Content-type", "text/html;charset=UTF-8");
                        response.getWriter().print(JSON.toJSONString(serverResponse));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }else {
                //对请求进行路由
                ctx.setSendZuulResponse(true);
            }
        }
        return null;
    }
}
