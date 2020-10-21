package com.hwj.tieba.filter.post;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.config.common.Constants;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;
import java.util.Map;

public class ModuleAdminFilter extends ZuulFilter {
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if(requestContext.sendZuulResponse()){
            if(request.getRequestURI().matches("/api/post/moduleAdmin/\\w*")){
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
        String moduleId = request.getParameter("moduleId");
        if(moduleId == null || moduleId.equals("")){
            requestContext.setSendZuulResponse(false);
            responseJson(response, ServerResponse.createByErrorMessage("参数错误"));
        }else {
            Map<String,String> sessionMap = redisUtil.hget(Constants.SPRING_SESSION_ID_TOKEN_PREFIX + request.getSession().getId());
            if(sessionMap.get("Account") == null){
                requestContext.setSendZuulResponse(false);
                responseJson(response,ServerResponse.createByErrorMessage("未登录"));
            }
            Account account = JSON.parseObject(sessionMap.get("Account"),Account.class);

            if(account.getRoleId().indexOf("3") == -1){
                requestContext.setSendZuulResponse(false);
                responseJson(response, ServerResponse.createByErrorMessage("无权操作"));
            }else {
                requestContext.setSendZuulResponse(true);
            }
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
