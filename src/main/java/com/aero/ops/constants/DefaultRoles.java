package com.aero.ops.constants;

import lombok.Getter;

@Getter
public enum DefaultRoles {

    ADMIN(1, "超级管理员", "拥有绝对权限和权限分配"),
    UNKNOWN(9, "未知成员", "首次扫描钉钉入库，但未确认身份，未开放任何权限"),
    ;


    private String roleName;
    private int roleId;
    private String remark;

    DefaultRoles(int roleId, String roleName, String remark) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.remark = remark;
    }
}
