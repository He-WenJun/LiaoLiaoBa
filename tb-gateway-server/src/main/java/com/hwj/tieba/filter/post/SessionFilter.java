package com.hwj.tieba.filter.post;

import com.hwj.tieba.config.common.Constants;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFilter extends ZuulFilter {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        //从请求上下文中获取SessionId
        String sessionID = ctx.getRequest().getSession().getId();
        //将SessionId写入请求头，后端服务通过请求头获取SessionId，从Redis中获取Session以达到Session共享目的
        ctx.addZuulRequestHeader("SessionId", Constants.SPRING_SESSION_ID_TOKEN_PREFIX +sessionID);
        //对请求进行路由
        ctx.setSendZuulResponse(true);
        return null;
    }
}
