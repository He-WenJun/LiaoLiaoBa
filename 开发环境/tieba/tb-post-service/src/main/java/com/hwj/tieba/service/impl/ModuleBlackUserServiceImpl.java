package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.ModuleBlackUserMapper;
import com.hwj.tieba.dao.ModuleUserRoleMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.ModuleBlackUser;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.ModuleBlackUserService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.ModuleBlackUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class ModuleBlackUserServiceImpl implements ModuleBlackUserService {
    @Autowired
    private ModuleBlackUserMapper moduleBlackUserMapper;
    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<PageInfo<ModuleBlackUserVo>> queryModuleBlackUserList(ModuleBlackUser moduleBlackUser, int pageNumber, Account searchUserName) {
        //如果需要搜索的用户名不为空，则查询这个用户名的Id
        if(searchUserName != null && searchUserName.getUserName() != null){
            ServerResponse<List<Account>> response = userService.getAccountList(searchUserName);
            if(response.getStatus() !=0){
                return ServerResponse.createByErrorMessage(response.getMsg());
            }
            if(response.getData().size()  == 0){
                return ServerResponse.createByErrorMessage("黑名单中没有这个用户");
            }
            moduleBlackUser.setUserId(response.getData().get(0).getUserId());
        }

        //查询黑名单Id
        Page page = PageHelper.startPage(pageNumber, Constants.pageCountSize, true);
        List<ModuleBlackUser> resultModuleBlackUserList = moduleBlackUserMapper.queryUserIdAndEnrollDateByModuleId(moduleBlackUser);
        if(resultModuleBlackUserList.size() == 0){
            return ServerResponse.createByErrorMessage("没有黑名单用户");
        }

        List<String> blackUserIdList = new ArrayList<>(Constants.pageCountSize);
        //取出黑名单id，调用user-service获取账号信息
        for(ModuleBlackUser mbu : resultModuleBlackUserList){
            blackUserIdList.add(mbu.getUserId());
        }
        List<AccountVo>  accountVoList  = userService.getUserInfoList(blackUserIdList).getData();

        //组装返回数据
        List<ModuleBlackUserVo> moduleBlackUserVoList = new ArrayList<>(Constants.pageCountSize);
        for (int i = 0; i < resultModuleBlackUserList.size(); i++) {
            for (int j = 0; j < accountVoList.size(); j++){
                if(resultModuleBlackUserList.get(i).getUserId().equals(accountVoList.get(j).getUserId())){
                    moduleBlackUserVoList.add(new ModuleBlackUserVo(accountVoList.get(j), resultModuleBlackUserList.get(i)));
                    break;
                }
            }
        }

        PageInfo<ModuleBlackUserVo> pageInfo = new PageInfo<>(page);
        pageInfo.setList(moduleBlackUserVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<String> deleteModuleBlackUser(ModuleBlackUser moduleBlackUser) {
        moduleBlackUserMapper.deleteModuleBlackUser(moduleBlackUser);
        redisUtil.getRedisTemplate().delete(Constants.POST_TOKEN_PREFIX + "MODULE_BLACK_USER:" + moduleBlackUser.getModuleId()+":" + moduleBlackUser.getUserId());
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    @Override
    public ServerResponse<String> addModuleBlackUser(ModuleBlackUser moduleBlackUser, Account searchUserName) {
        //调用user-service 查询出用户名对应的Id，若状态码不为0，查询出现了问题
        ServerResponse<List<Account>> response = userService.getAccountList(searchUserName);
        if(response.getStatus() != 0){
            return ServerResponse.createByErrorMessage(response.getMsg());
        }

        moduleBlackUser.setUserId(response.getData().get(0).getUserId());
        if(moduleBlackUserMapper.queryUserIdAndEnrollDateByModuleId(moduleBlackUser).size() != 0){
            return ServerResponse.createByErrorMessage("该用户已经在黑名单中了");
        }
        moduleBlackUser.setId(UUIDUtil.getStringUUID());
        Date nowDate = new Date();
        moduleBlackUser.setEnrollDate(nowDate);
        moduleBlackUser.setUpdateDate(nowDate);
        moduleBlackUserMapper.insertModuleBlackUser(moduleBlackUser);

        redisUtil.getRedisTemplate().opsForValue().set(Constants.POST_TOKEN_PREFIX + "MODULE_BLACK_USER:" + moduleBlackUser.getModuleId()+":" + moduleBlackUser.getUserId(), JSON.toJSONString(moduleBlackUser));


        return ServerResponse.createBySuccessMessage("已将此用户加入黑名单");
    }

    private Account getAccount(String sessionId){
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //判断当前session当中是否存在账户号json，若不存在则未登录
        if(sessionMap.get("Account") == null){
            throw new TieBaException("未登录");
        }
        return JSON.parseObject(sessionMap.get("Account"),Account.class);
    }
}
