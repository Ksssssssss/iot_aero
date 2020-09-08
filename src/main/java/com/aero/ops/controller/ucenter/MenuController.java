package com.aero.ops.controller.ucenter;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.ops.constants.MenuType;
import com.aero.ops.entity.dto.*;
import com.aero.ops.entity.po.MenuPO;
import com.aero.ops.entity.po.RolePO;
import com.aero.ops.entity.vo.MenuVO;
import com.aero.ops.entity.vo.UserVO;
import com.aero.ops.service.IMenuService;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import com.aero.ops.utils.TokenUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author 罗涛
 * @title UserController
 * @date 2020/7/3 18:35
 */
@Slf4j
@RestController
@RequestMapping(value = "menu")
@Api(tags={"菜单管理接口"}, value = "MenuController")
public class MenuController {

    @Autowired
    IMenuService menuService;


    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ApiOperation(value = "菜单列表")
    public String list(@RequestBody PageDTO pageDTO){
        PageModel<List<MenuPO>> serverByPage = menuService.getMenusByPage(pageDTO);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }

    @RequestMapping(value = "allMenuTree", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有菜单树形结构")
    public String getAllMenuTree(){
        List<MenuVO> menuTree = menuService.getMenuTree();
        return JSON.toJSONString(menuTree);
    }

    @RequestMapping(value = "getNavMenu", method = RequestMethod.GET)
    @ApiOperation(value = "根据登录用户角色获取菜单树形结构")
    public String getNavMenu(){
        UserVO loginUser = TokenUtils.getLoginUser();
//        List<MenuVO> menuTree = menuService.getMenuTree();
        List<MenuVO> menuTree = menuService.getMenuTreeByRole(loginUser.getRoleId());
        return JSON.toJSONString(menuTree);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ApiOperation(value = "添加菜单")
    public ResponseModel<Boolean> save(@RequestBody MenuSaveDTO saveDTO){
        try {
            MenuPO menuPO = new MenuPO();
            BeanUtils.copyProperties(saveDTO, menuPO);
            menuPO.setCrtTime(new Date());
            menuPO.setUpdTime(new Date());
            boolean save = menuService.save(menuPO);
            return ResultUtils.me.success(save);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.me.error(ResultState.EXCEPTION,e.getMessage(),Boolean.FALSE);
        }
    }


    @RequestMapping(value = "edit", method = RequestMethod.POST)
    @ApiOperation(value = "编缉菜单")
    public ResponseModel<Boolean> edit(@RequestBody MenuEditDTO editDTO){
        if(StringUtils.isEmpty(editDTO.getId())){
            return ResultUtils.me.error(ResultState.PARAM_ERROR, "id不能为空", null);
        }
        boolean flag = menuService.update(editDTO);
        return ResultUtils.me.success(flag);
    }


    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除菜单")
    public ResponseModel<Boolean> delete(@RequestParam(value = "menuIds") String menuIds){
        if(StringUtils.isEmpty(menuIds)){
            return ResultUtils.me.error(ResultState.PARAM_ERROR, "id不能为空", null);
        }
        List<String> uids = Arrays.asList(StringUtils.split(menuIds,","));
        boolean flag = menuService.delete(uids);
        return ResultUtils.me.success(flag);
    }


    @RequestMapping(value = "allParent", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有父菜单")
    public ResponseModel<List<MenuVO>> getAllParent(){
        List<MenuVO> parentMenus = menuService.getAllParent();
        return ResultUtils.me.success(parentMenus);
    }


    @RequestMapping(value = "allTypes", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有菜单类型")
    public ResponseModel<List<Map<String,Object>>> getAllTypes(){
        List<Map<String,Object>> types = new ArrayList<>();
        for(MenuType type : MenuType.values()){
            Map<String,Object> typeMap = new HashMap<>();
            typeMap.put("type", type.getType());
            typeMap.put("desc", type.getDesc());
            types.add(typeMap);
        }
        return ResultUtils.me.success(types);
    }

    @RequestMapping(value = "getById", method = RequestMethod.GET)
    @ApiOperation(value = "获取菜单")
    public ResponseModel<MenuPO> get(@RequestParam(value = "id") String id){
        MenuPO menu = menuService.getMenu(id);
        return ResultUtils.me.success(menu);
    }

}
