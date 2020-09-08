package com.aero.ops.controller.device;

import com.aero.common.constants.SensorProtocol;
import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.ops.entity.dto.DeviceEditDTO;
import com.aero.ops.entity.dto.DeviceQueryDTO;
import com.aero.ops.entity.po.DevicePO;
import com.aero.ops.entity.vo.SensorTypeVO;
import com.aero.ops.service.IDeviceService;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/device")
@Api(tags={"设备管理接口(SQL SERVER)"}, value = "DeviceController")
public class DeviceController {
    @Autowired
    IDeviceService deviceService;

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
        PageModel<List<DevicePO>> pageModel = deviceService.getDefaultDevices(pageDTO);
        ResponseModel result = ResultUtils.me.success(pageModel);
        return LayTableUtil.me.convertResult2LayTable(result);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(value = "根据查询条件筛选分页数据")
    public String getDefaultDevices(@RequestBody DeviceQueryDTO deviceQueryDTO){
        PageModel<List<DevicePO>> pageModel = deviceService.getDevicesByQuery(deviceQueryDTO);
        ResponseModel result = ResultUtils.me.success(pageModel);
        return LayTableUtil.me.convertResult2LayTable(result);
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "根据查询条件筛选分页数据")
    public ResponseModel<Boolean> getDefaultDevices(@RequestBody DeviceEditDTO deviceEditDTO){
        boolean editResult = deviceService.edit(deviceEditDTO);
        return ResultUtils.me.success(editResult);
    }


    @RequestMapping(value = "/sensorTypes", method = RequestMethod.GET)
    public ResponseModel<List<SensorTypeVO>> sensorTypes(){
        SensorProtocol[] protocols = SensorProtocol.values();
        List<SensorTypeVO> types = new ArrayList<>();
        for(SensorProtocol protocol:protocols){
            SensorTypeVO sensorTypeVO = new SensorTypeVO();
            sensorTypeVO.setCode(protocol.getSensorType());
            sensorTypeVO.setDesc(protocol.getName());
            types.add(sensorTypeVO);
        }
        return ResultUtils.me.success(types);
    }


}
