package com.aero.ops.service;


import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.DeviceQueryDTO;
import com.aero.ops.entity.dto.MongoDeviceEditDTO;
import com.aero.ops.entity.po.MongoDevicePO;

import java.util.List;

/**
 * @author 罗涛
 * @title DeviceService
 * @date 2020/6/28 19:27
 */
public interface IMongoDeviceService {
    /**
     * 获取所有可见设备
     * @return
     */
    PageModel<List<MongoDevicePO>> getDefaultDevices(PageDTO pageDTO);

    PageModel<List<MongoDevicePO>> getDevicesByQuery(DeviceQueryDTO queryDTO);

    boolean edit(MongoDeviceEditDTO editDTO);
}
