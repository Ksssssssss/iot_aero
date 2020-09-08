package com.aero.ops.entity.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 罗涛
 * @title CodeLibPO
 * @date 2020/7/16 18:27
 */

@Data
public class CodeLibEditDTO {
    private String id;
    private String projectName;
    private String versionControl;
    private String compiler;
    private String cloneType;
    private String url;
    private String userName;
    private String password;
}
