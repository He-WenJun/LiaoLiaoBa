package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.MenuMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.MenuParent;
import com.hwj.tieba.entity.MenuSon;
import com.hwj.tieba.entity.Menu_Role_Item;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.MenuService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.MenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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
            List<MenuVO> menuVO = redisUtil.get(Constants.TOKEN_PREFIX+"MENU:"+account.getUserId(),List.class);
            return ServerResponse.createBySuccess(menuVO);
        }

        //一个用户可以有多个角色
        //获取角色Id,Id以,号间隔,转换为Int
        List<Integer> roleIdList = new ArrayList<>(3);
        //若无逗号则表明只有一个角色，不需要拆分
        if(account.getRoleId().indexOf(",") != -1){
            for(String idStr : account.getRoleId().split(",")){
                roleIdList.add(Integer.valueOf(idStr));
            }
        }else {
            roleIdList.add(Integer.valueOf(account.getRoleId()));
        }

        //查询RoleId 对应的父菜单Id
        List<Menu_Role_Item> menu_role_itemList = menuMapper.queryMenuParentByRoleId(roleIdList);
        //查询父菜单
        List<MenuParent> menuParentList = menuMapper.queryMenuParentById(menu_role_itemList);

        //按父菜单Id查询对应的子菜单
        List<MenuSon> menuSonList = menuMapper.queryMenuSonByParentId(menuParentList);

        //修改父菜单项个人中心名称为当前登录的用户名,并组装菜单数据，一个父菜单对应多个子菜单
        List<MenuVO> menuVOList = new ArrayList<>(5);
        for(MenuParent menuParent : menuParentList){
            if(menuParent.getName().equals("Name")){
                menuParent.setName(account.getUserName());
            }
            List<MenuSon> temp = new ArrayList<>(3);
            for (MenuSon menuSon : menuSonList){
                if(menuParent.getId().equals(menuSon.getParentId())){
                    if(menuSon.getId() == 1){
                        menuSon.setResource(menuSon.getResource() + account.getUserId());
                    }
                    temp.add(menuSon);
                }
            }
            menuVOList.add(new MenuVO(menuParent, temp));
        }



        System.out.println("=======Menu=======:"+JSON.toJSONString(menuVOList));

        //将结果存入redis
        redisUtil.set(Constants.TOKEN_PREFIX+"MENU:"+account.getUserId(),60,JSON.toJSONString(menuVOList));

        return ServerResponse.createBySuccess(menuVOList);
    }
}
