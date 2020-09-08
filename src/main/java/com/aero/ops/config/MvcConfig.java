package com.aero.ops.config;

import com.aero.ops.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 罗涛
 * @title MvcConfig
 * @date 2020/8/5 11:07
 */

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    IMenuService menuService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new PrivilegeInterceptor(menuService));
        registration.addPathPatterns("/**/*");
        registration.excludePathPatterns(
                "/login.html", //登录页
                "/user/getDingTalkConfig", //获取钉钉配置
                "/user/getCaptchaImg",  //获取登录验证码
                "/user/loginByPwd",     //登录接口
                "/user/loginWithDing",  //登录接口
                "/error",               //错误页面
                "/**/*.js",              //js静态资源
                "/**/*.min.js",
                "/**/*.css",             //css静态资源
                "/**/*.map",
                "/**/*.woff",
                "/**/*.woff2",
                "/**/*.ttf",
                "/**/*.svg",
                "/**/*.eot",
                "/**/*.gif",
                "/**/*.png",
                "/**/*.jpg",
                "/**/*.ico",
                "/**/*.json",
                "/**/*.mp4"
        );
    }
}
