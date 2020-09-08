package com.aero.ops.service;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.ServerSaveDTO;
import com.aero.ops.entity.po.ServerPO;

import java.util.List;

public interface IServerService {
    /**
     * 获取服务器分页数据
     * @param pageDTO
     * @return
     */
    PageModel<List<ServerPO>> getServerByPage(PageDTO pageDTO);

    boolean save(ServerSaveDTO saveDTO) throws Exception;

    ServerPO getServerByLanIp(String lanIp);
}
