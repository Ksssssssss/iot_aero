package com.aero.ops.entity.dto;

import com.aero.ops.entity.po.ServerPO;
import lombok.Data;

import java.util.List;

/**
 * @author 罗涛
 * @title ProjectDeployInfo
 * @date 2020/7/8 9:13
 */
@Data
public class ProjectDeployInfo {
    private String projectName;
    private String codeLibrary;
    private String branch;
    private String compiler;
    private List<ServerPO> machines;
}
