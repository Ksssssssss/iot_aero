package com.aero.ops.controller.device;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.ops.entity.dto.DeviceEditDTO;
import com.aero.ops.entity.dto.DeviceQueryDTO;
import com.aero.ops.entity.dto.MongoDeviceEditDTO;
import com.aero.ops.entity.po.DevicePO;
import com.aero.ops.entity.po.MongoDevicePO;
import com.aero.ops.service.IDeviceService;
import com.aero.ops.service.IMongoDeviceService;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/mongoDevice")
@Api(tags={"设备管理接口(MongoDB)"}, value = "MongoDeviceController")
public class MongoDeviceController {
    @Autowired
    IMongoDeviceService mongoDeviceService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "查询默认分页数据")
    @ApiImplicitParams ({
        @ApiImplicitParam(name = "pageIndex", value = "当前页码", dataType="int", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "每页展示条目数量", dataType="int", required = true, paramType = "query")
    })
    public String getDefaultDevices(@RequestParam Integer pageIndex, @RequestParam Integer pageSize){
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageIndex(pageIndex);
        pageDTO.setPageSize(pageSize);
        PageModel<List<MongoDevicePO>> pageModel = mongoDeviceService.getDefaultDevices(pageDTO);
        ResponseModel result = ResultUtils.me.success(pageModel);
        return LayTableUtil.me.convertResult2LayTable(result);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(value = "根据查询条件筛选分页数据")
    public String getDefaultDevices(@RequestBody DeviceQueryDTO deviceQueryDTO){
        PageModel<List<MongoDevicePO>> pageModel = mongoDeviceService.getDevicesByQuery(deviceQueryDTO);
        ResponseModel result = ResultUtils.me.success(pageModel);
        return LayTableUtil.me.convertResult2LayTable(result);
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "根据查询条件筛选分页数据")
    public ResponseModel<Boolean> getDefaultDevices(@RequestBody MongoDeviceEditDTO deviceEditDTO){
        boolean editResult = mongoDeviceService.edit(deviceEditDTO);
        return ResultUtils.me.success(editResult);
    }
}
