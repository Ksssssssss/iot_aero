package com.aero.ops.service;

/**
 * @author 罗涛
 * @title IDingTalkService
 * @date 2020/7/10 13:54
 */
public interface IDingTalkService {

    String getAccessToken();

    String getUserInfo(String code) throws Exception;

    String getUninonId(String code, long timestamp) throws Exception;
}
