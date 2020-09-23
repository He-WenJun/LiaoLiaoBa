package com.hwj.tieba.filter.post;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.config.common.Constants;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.OthersSetting;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class UserOthersSettingFilter extends ZuulFilter {
    @Autowired
    RedisUtil redisUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        System.out.println(requestContext.sendZuulResponse());
        if(requestContext.sendZuulResponse()){
            if(request.getRequestURI().equals("/api/post/commitComment")) {
                return true;
            }else if(request.getRequestURI().equals("/api/post/commitReply")){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();
        Map<String,String> sessionMap = redisUtil.hget(Constants.SPRING_SESSION_ID_TOKEN_PREFIX + request.getSession().getId());
        if(sessionMap.get("Account") == null){
            requestContext.setSendZuulResponse(false);
            responseJson(response,ServerResponse.createByErrorMessage("未登录"));
        }
        Account account = JSON.parseObject(sessionMap.get("Account"),Account.class);

        String targetUserId = request.getParameter("targetUserId");
        if(targetUserId == null){
            requestContext.setSendZuulResponse(false);
            responseJson(response, ServerResponse.createByErrorMessage("参数错误"));
        }
        //判断帖子的发表用户是否设置了拒绝该用户评论回复我的帖子
        String settingKey = Constants.ACCOUNT_TOKEN_PREFIX + "OTHERS_SETTING:USER_ID_" + targetUserId + ":USER_ID" + account.getUserId();
        if(redisUtil.hasKey(settingKey)){
            requestContext.setSendZuulResponse(false);
            OthersSetting othersSetting = redisUtil.get(settingKey, OthersSetting.class);
            if(othersSetting.isCommentPost()){
                requestContext.setSendZuulResponse(false);
                responseJson(response, ServerResponse.createByErrorCodeMessage(1,"无权评论此贴"));
            }
        }else {
            requestContext.setSendZuulResponse(true);
        }
        return null;
    }

    private void responseJson(HttpServletResponse resp, ServerResponse serverResponse){
        try {
            resp.setHeader("Content-type", "text/html;charset=UTF-8");
            resp.getWriter().print(JSON.toJSONString(serverResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
