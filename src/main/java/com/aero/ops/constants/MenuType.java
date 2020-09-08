package com.aero.ops.constants;

import lombok.Getter;

/**
 * @author 罗涛
 * @title MenuType
 * @date 2020/8/5 14:51
 */

@Getter
public enum MenuType {
    PARENT(1, "父菜单"),
    NODE_PAGE(2, "节点页"),
    IFRAME_PAGE(3, "弹出页"),
    HOME_PAGE(4, "首页"),
    INTERFACE(5, "接口/按钮");

    int type;
    String desc;

    MenuType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
