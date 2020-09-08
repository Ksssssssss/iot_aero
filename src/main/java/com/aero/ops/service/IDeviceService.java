package com.aero.ops.service;


import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.DeviceEditDTO;
import com.aero.ops.entity.dto.DeviceQueryDTO;
import com.aero.ops.entity.po.DevicePO;

import java.util.List;

/**
 * @author 罗涛
 * @title DeviceService
 * @date 2020/6/28 19:27
 */
public interface IDeviceService {
    /**
     * 获取所有可见设备
     * @return
     */
    PageModel<List<DevicePO>> getDefaultDevices(PageDTO pageDTO);

    PageModel<List<DevicePO>> getDevicesByQuery(DeviceQueryDTO queryDTO);

    boolean edit(DeviceEditDTO editDTO);
}
