package com.aero.ops.entity.dto;

import lombok.Data;

@Data
public class MenuSaveDTO {
    private String title;
    private String icon;
    private String href;
    private Boolean spread;
    private String parentId;
    private Integer menuType;
}
