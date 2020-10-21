package com.hwj.tieba.filter.post;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.config.common.Constants;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.ModuleBlackUser;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ModuleBlackUserFilter extends ZuulFilter {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 4;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if(requestContext.sendZuulResponse()){
            if(request.getRequestURI().equals("/api/post/commitPost")){
                return true;
            }else if(request.getRequestURI().equals("/api/post/commitComment")){
                return true;
            }else if(request.getRequestURI().equals("/api/post/commitReply")) {
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
        System.out.println(Constants.SPRING_SESSION_ID_TOKEN_PREFIX + request.getHeader("SessionId"));
        Map<String,String> sessionMap = redisUtil.hget(Constants.SPRING_SESSION_ID_TOKEN_PREFIX + request.getSession().getId());
        if(sessionMap.get("Account") == null){
            requestContext.setSendZuulResponse(false);
            responseJson(response, ServerResponse.createByErrorMessage("未登录"));
            return null;
        }
        Account account = JSON.parseObject(sessionMap.get("Account"),Account.class);

        String moduleId = request.getParameter("moduleId");
        if(moduleId == null){
            requestContext.setSendZuulResponse(false);
            responseJson(response, ServerResponse.createByErrorMessage("参数错误"));
            return null;
        }
        String key = Constants.POST_TOKEN_PREFIX + "MODULE_BLACK_USER:" + moduleId +":" + account.getUserId();
        if(redisUtil.hasKey(key)) {
            requestContext.setSendZuulResponse(false);
            requestContext.set("sendForwardFilter.ran", true);
            ModuleBlackUser moduleBlackUser = redisUtil.get(key, ModuleBlackUser.class);
            responseJson(response,ServerResponse.createByErrorMessage("你被列入了本模块黑名单，无权发表任何信息<br/>列入黑名单原因：" + moduleBlackUser.getDescribe()));
            return null;
        }
        requestContext.setSendZuulResponse(true);

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
