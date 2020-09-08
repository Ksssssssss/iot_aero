package com.aero.ops.controller.monitor;

import com.aero.common.model.ResponseModel;
import com.aero.common.model.ResultState;
import com.aero.ops.entity.dto.ClusterEditDTO;
import com.aero.ops.entity.dto.ClusterSaveDTO;
import com.aero.ops.entity.po.ClusterPO;
import com.aero.ops.service.IClusterService;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/cluster")
@Api(tags={"集群管理接口"}, value = "ClusterController")
public class ClusterController {

    @Autowired
    IClusterService clusterService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<ClusterPO> getAll(){
        return clusterService.getAll();
    }

    @RequestMapping(value = "/getAllCluster", method = RequestMethod.GET)
    public ResponseModel<List<ClusterPO>> getAllCluster(){
        List<ClusterPO> allCluster = clusterService.getAllCluster();
        return ResultUtils.me.success(allCluster);
    }


    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public ResponseModel<ClusterPO> getById(@RequestParam(value = "id") String id){
        ClusterPO cluster = clusterService.getById(id);
        return ResultUtils.me.success(cluster);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseModel<ClusterPO> save(@RequestBody ClusterSaveDTO saveDTO){
        ClusterPO save = clusterService.save(saveDTO);
        return ResultUtils.me.success(save);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResponseModel<Boolean> edit(@RequestBody ClusterEditDTO editDTO){
        Boolean flag = clusterService.edit(editDTO);
        return ResultUtils.me.success(flag);
    }


    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除节点")
    public ResponseModel<Boolean> delete(@RequestParam(value = "clusterIds") String clusterIds){
        if(StringUtils.isEmpty(clusterIds)){
            return ResultUtils.me.error(ResultState.PARAM_ERROR, "id不能为空", null);
        }
        List<String> cids = Arrays.asList(StringUtils.split(clusterIds,","));
        boolean flag = clusterService.delete(cids);
        return ResultUtils.me.success(flag);
    }
}
