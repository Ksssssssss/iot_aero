package com.aero.ops.service;

import com.aero.common.logger.DeviceLog;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.AppLogQueryDTO;
import com.aero.ops.entity.dto.DeviceLogQueryDTO;
import com.aero.ops.entity.po.AppLog;
import com.aero.ops.entity.vo.AppLogVO;

import java.util.List;

public interface IDevcieLogService {

    /***
     * 查询微服务日志
     * @param queryDTO 日志查询条件
     * @return
     */
    PageModel<List<DeviceLog>> queryDeviceLogs(DeviceLogQueryDTO queryDTO);
}
