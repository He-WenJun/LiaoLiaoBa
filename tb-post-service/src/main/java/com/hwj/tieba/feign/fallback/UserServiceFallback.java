package com.hwj.tieba.feign.fallback;

import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.AccountVo;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * user-service 服务降级
 */
@Component
public class UserServiceFallback implements UserService {

    @Override
    public ServerResponse increaseAccountExp(Integer increaseExp, String token, String userId) {
        return ServerResponse.createByErrorMessage("账号经验值增加失败");
    }

    @Override
    public ServerResponse<List<AccountVo>> getUserInfoList(List<String> userIdList) {
        return ServerResponse.createByErrorMessage("获取用户信息失败");
    }
}
