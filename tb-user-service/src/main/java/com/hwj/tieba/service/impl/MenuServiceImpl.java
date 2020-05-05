package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.MenuMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.MenuParent;
import com.hwj.tieba.entity.MenuSon;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.MenuService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.MenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse getMenu(String sessionId) {
        //获取redis中的session对象
        Map<String,String> sessionMap = (Map<String, String>) redisUtil.hget(sessionId);
        Account account = JSON.parseObject(sessionMap.get("Account"),Account.class);

        //若用户角色Id或用户名为空则直接返回一个未登录菜单项
        if(StringUtils.isEmpty(account)){
            ServerResponse serverResponse = ServerResponse.createBySuccess("未登录");
            serverResponse.setStatus(1);
            return serverResponse;
        }

        //若redis中包含菜单信息则直接返回，不再查询
        if(redisUtil.hasKey(Constants.TOKEN_PREFIX+"MENU:"+account.getUserId())){
            MenuVO menuVO = redisUtil.get(Constants.TOKEN_PREFIX+"MENU:"+account.getUserId(),MenuVO.class);
            return ServerResponse.createBySuccess(menuVO);
        }


        //获取角色Id
        String roleIdStr = account.getRoleId();
        String userName = account.getUserName();

        //角色Id是一串数字字符串，一个用户可以有多个角色
        char[] roleIdArray = roleIdStr.toCharArray();
        List<Integer> menuParentIdList = new ArrayList<Integer>();
        for (char roleIdCha:roleIdArray){
            int roleIdInt = Integer.parseInt(String.valueOf(roleIdCha));
            //是否为普通用户
            if(roleIdInt == Constants.RoleType.ORDINARY){
                menuParentIdList.add(1);
                menuParentIdList.add(2);
                menuParentIdList.add(3);
                continue;
            }
            //是否为贴吧管理员
            if(roleIdInt == Constants.RoleType.HELPER || roleIdInt == Constants.RoleType.MASTER){
                menuParentIdList.add(4);
                continue;
            }
            //是否为系统管理员
            if(roleIdInt == Constants.RoleType.ADMIN){
                menuParentIdList.add(5);
                continue;
            }
        }

        //按父菜单Id查询父菜单
        List<MenuParent> menuParentList = menuMapper.queryMenuParentById(menuParentIdList);
        //修改父菜单项个人中心名称为当前登录的用户名
        for(MenuParent menuParent : menuParentList){
            if(menuParent.getName().equals("Name")){
                menuParent.setName(userName);
                break;
            }
        }

        //按父菜单Id查询对应的子菜单
        List<MenuSon> menuSonList = menuMapper.queryMenuSonByParentId(menuParentIdList);

        MenuVO menuVO = new MenuVO(menuParentList,menuSonList);

        //将结果存入redis
        redisUtil.set(Constants.TOKEN_PREFIX+"MENU:"+account.getUserId(),60,JSON.toJSONString(menuVO));

        return ServerResponse.createBySuccess(menuVO);
    }
}
