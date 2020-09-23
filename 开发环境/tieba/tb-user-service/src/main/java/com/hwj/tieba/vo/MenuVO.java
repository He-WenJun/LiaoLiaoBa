package com.hwj.tieba.vo;

import com.hwj.tieba.entity.MenuParent;
import com.hwj.tieba.entity.MenuSon;

import java.util.List;

public class MenuVO {
    private MenuParent menuParent;
    private List<MenuSon> menuSonsList;

    public MenuVO (){}
    public MenuVO (MenuParent menuParent , List<MenuSon> menuSonsList){
        this.menuParent = menuParent;
        this.menuSonsList = menuSonsList;
    }

    public MenuParent getMenuParent() {
        return menuParent;
    }

    public void setMenuParent(MenuParent menuParent) {
        this.menuParent = menuParent;
    }

    public List<MenuSon> getMenuSonsList() {
        return menuSonsList;
    }

    public void setMenuSonsList(List<MenuSon> menuSonsList) {
        this.menuSonsList = menuSonsList;
    }
}
