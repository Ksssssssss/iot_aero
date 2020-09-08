package com.aero.ops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 罗涛
 * @title DingTalkConfig
 * @date 2020/8/3 14:56
 */

@Configuration
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkConfig {

//    public static final String appid = "dingoa07iqeed43vmofobw";
//    public static final String appSecret = "jHhCdHxbFSTMXeW1iaB18yAITCFfLS6d29ebKnF3xTD-8fC7oa-ZZN0Wee0ybNwf";

    private String appid;
    private String appSecret;
    private String baseUrl;
    private String encodeBaseUrl;


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getEncodeBaseUrl() {
        return encodeBaseUrl;
    }

    public void setEncodeBaseUrl(String encodeBaseUrl) {
        this.encodeBaseUrl = encodeBaseUrl;
    }
}
