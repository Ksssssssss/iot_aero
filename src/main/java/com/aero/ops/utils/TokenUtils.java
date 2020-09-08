package com.aero.ops.utils;

import com.aero.common.utils.EncryptUtil;
import com.aero.ops.entity.vo.UserVO;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 罗涛
 * @title TokenUtils
 * @date 2020/8/5 9:49
 */
public class TokenUtils {

    public static String getToken(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = (String)request.getSession().getAttribute("token");
        return token;
    }

    public static UserVO getLoginUser(){
        String token = getToken();
        if(StringUtils.isEmpty(token)){
            return null;
        }
        String decUserJson = EncryptUtil.decrypt(token);
        UserVO userVO = JSON.parseObject(decUserJson,UserVO.class);
        return userVO;
    }
}
