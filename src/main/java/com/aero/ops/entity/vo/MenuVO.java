package com.aero.ops.entity.vo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class MenuVO {
    private String id;
    private String title;
    private String icon;
    private String href;
    private Boolean spread;
    private Integer menuType;
    private List<MenuVO> children;
}
