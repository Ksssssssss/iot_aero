package com.aero.ops.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "Menu")
public class MenuEditDTO {
    @Id
    private String id;
    private String title;
    private String icon;
    private String href;
    private String parentId;
    private Integer menuType;
    private Boolean spread;
}
