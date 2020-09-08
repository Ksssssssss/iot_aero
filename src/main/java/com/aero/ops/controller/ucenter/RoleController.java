package com.aero.ops.controller.ucenter;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.ops.entity.dto.*;
import com.aero.ops.entity.po.RolePO;
import com.aero.ops.entity.po.UserPO;
import com.aero.ops.entity.vo.UserVO;
import com.aero.ops.service.IDingTalkService;
import com.aero.ops.service.IRoleService;
import com.aero.ops.service.IUserService;
import com.aero.ops.utils.AuthcodeImgUtil;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping(value = "role")
@Api(tags={"角色管理接口"}, value = "RoleController")
public class RoleController {

    @Autowired
    IRoleService roleService;


    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ApiOperation(value = "角色列表")
    public String list(@RequestBody PageDTO pageDTO){
        PageModel<List<RolePO>> serverByPage = roleService.getRolesByPage(pageDTO);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }


    @RequestMapping(value = "query", method = RequestMethod.POST)
    @ApiOperation(value = "角色列表")
    public String query(@RequestBody RoleQueryDTO queryDTO){
        PageModel<List<RolePO>> serverByPage = roleService.getRolesByQuery(queryDTO);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }


    @RequestMapping(value = "getById", method = RequestMethod.GET)
    @ApiOperation(value = "获取角色")
    public ResponseModel<RolePO> get(@RequestParam(value = "id") long id){
        RolePO role = roleService.getRole(id);
        return ResultUtils.me.success(role);
    }


    @RequestMapping(value = "edit", method = RequestMethod.POST)
    @ApiOperation(value = "编缉角色")
    public ResponseModel<Boolean> edit(@RequestBody @Valid RoleEditDTO editDTO, BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                return ResultUtils.error(ResultState.PARAM_ERROR, bindingResult.getAllErrors().get(0).getDefaultMessage());
            }
            boolean edit = roleService.edit(editDTO);
            return ResultUtils.me.success(edit);
        } catch (Exception e) {
            return ResultUtils.me.error(ResultState.EXCEPTION,e.getMessage(), false);
        }
    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ApiOperation(value = "新增角色")
    public ResponseModel<Boolean> edit(@RequestBody RoleSaveDTO saveDTO){
        try {
            boolean save = roleService.save(saveDTO);
            return ResultUtils.me.success(save);
        } catch (Exception e) {
            return ResultUtils.me.error(ResultState.EXCEPTION,e.getMessage(), false);
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色")
    public ResponseModel<Boolean> delete(@RequestParam(value = "roleIds") String roleIds){
        try {
            List<Long> ids = new ArrayList<>();
            for (String idStr : StringUtils.split(roleIds,",")) {
                long id = Long.valueOf(idStr);
                ids.add(id);
            }
            boolean delete = roleService.delete(ids);
            return ResultUtils.me.success(delete);
        } catch (Exception e) {
            return ResultUtils.me.error(ResultState.EXCEPTION,e.getMessage(), false);
        }
    }
}
