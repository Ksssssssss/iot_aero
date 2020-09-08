package com.aero.ops.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RoleEditDTO {

    @NotNull(message = "角色Id不能为空")
    private Long id;
    private String roleName;
    private Boolean enable;
    private String remark;
    private List<String> menuList;
}
