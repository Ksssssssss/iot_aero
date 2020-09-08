package com.aero.ops.controller.deploy;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.common.utils.EncryptUtil;
import com.aero.ops.entity.dto.ServerSaveDTO;
import com.aero.ops.entity.po.ServerPO;
import com.aero.ops.service.IServerService;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/server")
@Api(tags={"服务器管理"}, value = "ServerController")
public class ServerController{

    @Autowired
    IServerService serverService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation(value = "可用服务器列表")
    public String list(@RequestBody PageDTO pageDTO){
        PageModel<List<ServerPO>> serverByPage = serverService.getServerByPage(pageDTO);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation(value = "所有服务器列表")
    public String all(){
        PageModel<List<ServerPO>> serverByPage = serverService.getServerByPage(null);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ApiOperation(value = "新增服务器")
    public ResponseModel<Boolean> save(@RequestBody ServerSaveDTO saveDTO){
        try {
            String srcPwd = saveDTO.getPassword();
            String encPwd = EncryptUtil.encrypt(srcPwd);
            saveDTO.setPassword(encPwd);
            boolean saveFlag = serverService.save(saveDTO);
            return ResultUtils.me.success(saveFlag);
        } catch (Exception e) {
            log.error("添加服务器时发生异常，e={}", e);
            return ResultUtils.me.error(ResultState.EXCEPTION,e.getMessage(), false);
        }

    }


}
