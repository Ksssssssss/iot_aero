package com.aero.ops.controller.deploy;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.ops.entity.dto.CodeLibEditDTO;
import com.aero.ops.entity.dto.CodeLibSaveDTO;
import com.aero.ops.entity.po.CodeLibPO;
import com.aero.ops.entity.po.ServerPO;
import com.aero.ops.service.IBatchFileService;
import com.aero.ops.service.ICodeLibService;
import com.aero.ops.service.IServerService;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/codeLib")
@Api(tags={"代码库管理"}, value = "CodeLibController")
public class CodeLibController {

    @Autowired
    ICodeLibService codeLibService;

    @Autowired
    IServerService serverService;

    @Autowired
    IBatchFileService batchFileService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation(value = "可用代码库列表")
    public String list(@RequestBody PageDTO pageDTO){
        PageModel<List<CodeLibPO>> serverByPage = codeLibService.getCodeByPage(pageDTO);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ApiOperation(value = "新增代码库")
    public ResponseModel<Boolean> save(@RequestBody CodeLibSaveDTO saveDTO){
        try {
//            String srcPwd = saveDTO.getPassword();
//            String encPwd = EncryptUtil.encrypt(srcPwd);
//            saveDTO.setPassword(encPwd);
            boolean saveFlag = codeLibService.save(saveDTO);
            return ResultUtils.me.success(saveFlag);
        } catch (Exception e) {
            log.error("添加代码库时发生异常，e={}", e);
            return ResultUtils.me.error(ResultState.EXCEPTION,e.getMessage(), false);
        }

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation(value = "所有项目代码库")
    public String all(){
        PageModel<List<CodeLibPO>> serverByPage = codeLibService.getCodeByPage(null);
        ResponseModel responseModel = ResultUtils.me.success(serverByPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }


    @RequestMapping(value = "edit", method = RequestMethod.POST)
    @ApiOperation(value = "修改代码库信息")
    public ResponseModel<Boolean> edit(@RequestBody CodeLibEditDTO editDTO){
        if(StringUtils.isEmpty(editDTO.getId())){
            return ResultUtils.me.error(ResultState.PARAM_ERROR, "id不能为空", null);
        }
        boolean flag = codeLibService.update(editDTO);
        return ResultUtils.me.success(flag);
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除代码库")
    public ResponseModel<Boolean> delete(@RequestParam(value = "codeIds") String codeIds){
        if(StringUtils.isEmpty(codeIds)){
            return ResultUtils.me.error(ResultState.PARAM_ERROR, "id不能为空", null);
        }
        List<String> cids = Arrays.asList(StringUtils.split(codeIds,","));
        boolean flag = codeLibService.delete(cids);
        return ResultUtils.me.success(flag);
    }

    @RequestMapping(value = "getById", method = RequestMethod.GET)
    @ApiOperation(value = "获取代码库")
    public ResponseModel<CodeLibPO> get(@RequestParam(value = "id") String id){
        CodeLibPO codeLib = codeLibService.getCodeLib(id);
        return ResultUtils.me.success(codeLib);
    }


    @RequestMapping(value = "getBranches", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有分支")
    public ResponseModel<List<String>> getBranches(@RequestParam(value = "projectName") String projectName){
        try {
            String localIp = System.getProperty("local-ip");
            ServerPO localServer = serverService.getServerByLanIp(localIp);
            String codeBasePath = localServer.getCodeBasePath();
            CodeLibPO codeLib = codeLibService.getCodeLibByName(projectName);
            String projectCodePath = StringUtils.joinWith("/", codeBasePath, codeLib.getProjectName());
            File projectCodeFile = new File(projectCodePath);
            List<String> branches = null;
            if(projectCodeFile.exists()){
                branches = batchFileService.gitPruneAndBranches(projectCodePath);
            }else {
                projectCodeFile.mkdirs();
                String url = codeLib.getUrl();
                branches = batchFileService.gitCloneAndBranches(codeBasePath, projectName, url);
            }
            return ResultUtils.me.success(branches);
        } catch (Exception e) {
            log.error("获取项目分支时发生异常：{}, e = {}", e.getMessage(), e);
            return ResultUtils.error(ResultState.EXCEPTION);
        }
    }





}
