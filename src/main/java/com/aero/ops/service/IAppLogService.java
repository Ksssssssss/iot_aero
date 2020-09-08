package com.aero.ops.service;

import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.AppLogQueryDTO;
import com.aero.ops.entity.po.AppLog;
import com.aero.ops.entity.vo.AppLogVO;

import java.util.List;

public interface IAppLogService {
    /**
     * 获取所有微服务名称
     * @return
     */
    List<String> getAllApps();

    /***
     * 查询微服务日志
     * @param queryDTO 日志查询条件
     * @return
     */
    PageModel<List<AppLog>> queryAppLogs(AppLogQueryDTO queryDTO);


    PageModel<List<AppLogVO>> queryMergeLogs(AppLogQueryDTO queryDTO);
}
