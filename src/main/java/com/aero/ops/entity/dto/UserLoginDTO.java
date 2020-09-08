package com.aero.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 罗涛
 * @title UserLoginDTO
 * @date 2020/7/3 19:00
 */
@Data
@ApiModel("UserLoginDTO-用户登录信息")
public class UserLoginDTO {
    @NotNull(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    @NotNull(message = "密码不能为空")
    @ApiModelProperty("登录密码")
    private String password;

    @NotNull(message = "校验码不能为空")
    @ApiModelProperty("校验码")
    private String captcha;
}
