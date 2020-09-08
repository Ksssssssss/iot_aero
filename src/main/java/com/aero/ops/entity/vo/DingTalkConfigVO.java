package com.aero.ops.entity.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 罗涛
 * @title DingTalkConfig
 * @date 2020/8/3 14:56
 */


@Data
public class DingTalkConfigVO {

    private String appid;
    private String baseUrl;
    private String encodeBaseUrl;
}
