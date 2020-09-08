package com.aero.ops.controller.ucenter;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.common.utils.EncryptUtil;
import com.aero.ops.config.DingTalkConfig;
import com.aero.ops.constants.DefaultRoles;
import com.aero.ops.entity.dto.UserEditDTO;
import com.aero.ops.entity.dto.UserLoginDTO;
import com.aero.ops.entity.dto.UserQueryDTO;
import com.aero.ops.entity.po.UserPO;
import com.aero.ops.entity.vo.DingTalkConfigVO;
import com.aero.ops.entity.vo.UserVO;
import com.aero.ops.service.IDingTalkService;
import com.aero.ops.service.IUserService;
import com.aero.ops.utils.AuthcodeImgUtil;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import com.aero.ops.utils.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * @author 罗涛
 * @title UserController
 * @date 2020/7/3 18:35
 */
@Slf4j
@RestController
@RequestMapping(value = "user")
@Api(tags={"用户管理接口"}, value = "UserController")
public class UserController {
    public static final String TOPAUTHPWD = "devops@aero#2020";
    public static final String CAPTCHAKEY = "loginCaptchaImg";

    @Autowired
    IDingTalkService dingTalkService;

    @Autowired
    IUserService userService;

    @Autowired
    DingTalkConfig dingTalkConfig;

    @PostMapping("loginByPwd")
    public ResponseModel<UserVO> login(@RequestBody @Valid UserLoginDTO user, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResultUtils.error(ResultState.FAIL, errorMessage);
        }
        String username = user.getUsername();
        String password = user.getPassword();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String loginCaptcha = ((String) request.getSession().getAttribute(CAPTCHAKEY));
        if(!loginCaptcha.equalsIgnoreCase(user.getCaptcha())){
            return ResultUtils.error(ResultState.FAIL, "验证码错误！");
        }
        UserPO adminUser = userService.getUserByName(username);
        if(Objects.isNull(adminUser)){
            return ResultUtils.error(ResultState.FAIL, "该用户不是管理员，请用钉钉登录！");
        }
        if(!TOPAUTHPWD.equals(password)){
            return ResultUtils.error(ResultState.FAIL, "登录密码错误！");
        }
        userService.updateLastLoginTime(adminUser.getId());
        UserVO userVO = new UserVO();
        userVO.setRealName(username);
        userVO.setRoleId(DefaultRoles.ADMIN.getRoleId());
        userVO.setRoleName(DefaultRoles.ADMIN.getRoleName());
        userVO.setEnable(Boolean.TRUE);
        userVO.setPhoneNumber(adminUser.getPhoneNumber());
        userVO.setDingTalkUid(adminUser.getDingTalkUid());
        userVO.setLastLoginTime(new Date());
        String encUser = EncryptUtil.encrypt(JSON.toJSONString(userVO));
        HttpSession session = request.getSession();
        //设置session的有效期.备注（单位：秒。）
        session.setAttribute("token", encUser);
        session.setMaxInactiveInterval(4*60*60);
        return ResultUtils.me.success(userVO);
    }



    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ApiOperation(value = "成员列表")
    public String list(@RequestBody PageDTO pageDTO){
        PageModel<List<UserVO>> serverByPage = userService.getUsersByPage(pageDTO);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }


    @RequestMapping(value = "query", method = RequestMethod.POST)
    @ApiOperation(value = "成员列表")
    public String query(@RequestBody UserQueryDTO queryDTO){
        PageModel<List<UserVO>> serverByPage = userService.getUsersByQuery(queryDTO);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }


    @RequestMapping(value = "loginWithDing", method = RequestMethod.POST)
    public ResponseModel<UserVO> dingLogin(@RequestBody Map<String,String> ding){
        try {
            String code = ding.get("code");
            String dingUserInfoResp = dingTalkService.getUserInfo(code);
            JSONObject jsonObject = JSON.parseObject(dingUserInfoResp);
            int errorcode = (int) jsonObject.get("errcode");
            if(errorcode!=0){
                String errmsg = (String) jsonObject.get("errmsg");
                return ResultUtils.me.error(ResultState.FAIL, errmsg, null);
            }
            String userJson = jsonObject.getString("user_info");
            JSONObject user = JSON.parseObject(userJson);
            String name = (String) user.get("nick");
            String dingId = (String) user.get("dingId");
            UserPO dingUser = userService.getUserByDingId(dingId);
            if(Objects.nonNull(dingUser)){
                if(!dingUser.getEnable()){
                    log.warn("钉钉用户还未激活：nick = {}, dingId = {}", name, dingId);
                    return ResultUtils.me.error(ResultState.FAIL, "您的运维权限还未激活，请联系管理员！", null);
                }
                userService.updateLastLoginTime(dingUser.getId());
                UserVO userVO = new UserVO();
                userVO.setRealName(name);
                userVO.setRoleId(dingUser.getRoleId());
                userVO.setEnable(dingUser.getEnable());
                userVO.setDingNickName(dingUser.getDingNickName());
                userVO.setPhoneNumber(dingUser.getPhoneNumber());
                userVO.setDingTalkUid(dingUser.getDingTalkUid());
                userVO.setLastLoginTime(new Date());
                String encUser = EncryptUtil.encrypt(JSON.toJSONString(userVO));
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                assert servletRequestAttributes != null;
                HttpServletRequest request = servletRequestAttributes.getRequest();
                HttpSession session = request.getSession();
                //设置session的有效期.备注（单位：秒。）
                session.setAttribute("token", encUser);
                session.setMaxInactiveInterval(4*60*60);
                return ResultUtils.me.success(userVO);
            }else {
                log.warn("钉钉用户还未库：nick = {}, dingId = {}", name, dingId);
                UserPO newUser = new UserPO();
                newUser.setRealName(name);
                newUser.setDingNickName(name);
                newUser.setDingTalkUid(dingId);
                newUser.setEnable(Boolean.FALSE);
                newUser.setCrtTime(new Date());
                newUser.setRoleId(DefaultRoles.UNKNOWN.getRoleId());
                userService.save(newUser);
                return ResultUtils.me.error(ResultState.FAIL, "您还未申请运维权限", null);
            }
        } catch (Exception e) {
            log.error("钉钉扫码登录发生异常：{0}", e);
            return ResultUtils.me.error(ResultState.EXCEPTION, e.getMessage(), null);
        }
    }

    @GetMapping("getCaptchaImg")
    public void getCaptchaImg() throws Exception {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert servletRequestAttributes != null;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpServletResponse response = servletRequestAttributes.getResponse();

            char[] arr = new char[4];// 生成四位编码
            String authCode = new String(AuthcodeImgUtil.getAuthCode(arr));
            //把验证码存储到session
            request.getSession().setAttribute(CAPTCHAKEY, authCode);
            ImageIO.write(AuthcodeImgUtil.getIMG(authCode), "JPEG", response.getOutputStream());
        } catch (IOException e) {
            log.error("获取验证码图片时出错！{0}", e);
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseModel<Boolean> logout(){
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert servletRequestAttributes != null;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            request.getSession().removeAttribute("token");
            return ResultUtils.ok();
        } catch (Exception e){
            return ResultUtils.error(ResultState.EXCEPTION, e.getMessage());
        }
    }


    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public ResponseModel<Boolean> edit(@RequestBody UserEditDTO editDTO){
        if(StringUtils.isEmpty(editDTO.getId())){
            return ResultUtils.me.error(ResultState.PARAM_ERROR, "用户id不能为空", null);
        }
        boolean flag = userService.update(editDTO);
        return ResultUtils.me.success(flag);
    }


    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public ResponseModel<Boolean> delete(@RequestParam(value = "userIds") String userIds){
        if(StringUtils.isEmpty(userIds)){
            return ResultUtils.me.error(ResultState.PARAM_ERROR, "用户id不能为空", null);
        }
        List<String> uids = Arrays.asList(StringUtils.split(userIds,","));
        boolean flag = userService.delete(uids);
        return ResultUtils.me.success(flag);
    }


    @GetMapping("scanDingTalk")
    public ResponseModel<String> scanDingTalk(@PathVariable(value = "code") String code){
        try {
            String userInfo = dingTalkService.getUserInfo(code);
            JSONObject jsonObject = JSON.parseObject(userInfo);
            String user = jsonObject.getString("userInfo");


//        CorpUserDetail userDetail = UserAuthHelper.getCorpUserDetail(code);
//        if(userDetail==null){
//            throw new Exception("钉钉扫描获取用户信息失败！");
//        }
//        this.checkUserIdExist(userDetail.getUserid());
//        CorpUser corpUser = new CorpUser();
//        BeanUtils.copyProperties(userDetail, corpUser);
//        log.info("钉钉扫描corpUser:{}", JSONObject.toJSONString(corpUser));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("getDingTalkConfig")
    public ResponseModel<DingTalkConfigVO> getDingTalkConfig() throws Exception {
        DingTalkConfigVO config = new DingTalkConfigVO();
        BeanUtils.copyProperties(dingTalkConfig, config);
        return ResultUtils.me.success(config);
    }

}
