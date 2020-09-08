package com.aero.ops.config;

import com.aero.ops.entity.vo.UserVO;
import com.aero.ops.service.IMenuService;
import com.aero.ops.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author 罗涛
 * @title AdminInterceptor
 * @date 2020/8/5 10:56
 */
@Slf4j
public class PrivilegeInterceptor implements HandlerInterceptor {

    private IMenuService menuService;

    public PrivilegeInterceptor(IMenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        try {
            //统一拦截（查询当前session是否存在user）(这里user会在每次登陆成功后，写入session)
            UserVO loginUser = TokenUtils.getLoginUser();
            if(loginUser!=null){
                if("/index.html".equals(uri)){
                    return true;
                }
                long roleId = loginUser.getRoleId();
                List<String> accessUrls = menuService.getAccessUrls(roleId);
                if(accessUrls.contains(uri)){
                    return true;
                }
            }
            response.sendRedirect(request.getContextPath() + "/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.warn("访问url：{}受限，请确认！", uri);
        //如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
        //如果设置为true时，请求将会继续执行后面的操作
        return false;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

}