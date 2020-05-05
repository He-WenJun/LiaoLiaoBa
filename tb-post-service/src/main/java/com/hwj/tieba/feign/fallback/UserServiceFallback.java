package com.hwj.tieba.feign.fallback;

import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import org.springframework.stereotype.Component;


/**
 * user-service 服务降级
 */
@Component
public class UserServiceFallback implements UserService {

    @Override
    public ServerResponse increaseAccountExp(Integer increaseExp, String token, String userId) {
        return ServerResponse.createByErrorMessage("账号经验值增加失败");
    }
}
