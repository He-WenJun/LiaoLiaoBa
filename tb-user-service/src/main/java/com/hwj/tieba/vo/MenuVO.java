package com.hwj.tieba.vo;

import com.hwj.tieba.entity.MenuParent;
import com.hwj.tieba.entity.MenuSon;

import java.util.List;

public class MenuVO {
    private List<MenuParent> menuParentList;
    private List<MenuSon> menuSonsList;

    public MenuVO (){}
    public MenuVO (List<MenuParent> menuParentList , List<MenuSon> menuSonsList){
        this.menuParentList = menuParentList;
        this.menuSonsList = menuSonsList;
    }

    public List<MenuParent> getMenuParentList() {
        return menuParentList;
    }

    public void setMenuParentList(List<MenuParent> menuParentList) {
        this.menuParentList = menuParentList;
    }

    public List<MenuSon> getMenuSonsList() {
        return menuSonsList;
    }

    public void setMenuSonsList(List<MenuSon> menuSonsList) {
        this.menuSonsList = menuSonsList;
    }
}
