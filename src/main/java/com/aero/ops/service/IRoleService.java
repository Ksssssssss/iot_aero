package com.aero.ops.service;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.RoleEditDTO;
import com.aero.ops.entity.dto.RoleQueryDTO;
import com.aero.ops.entity.dto.RoleSaveDTO;
import com.aero.ops.entity.po.RolePO;

import java.util.List;

public interface IRoleService {
    RolePO getRole(long roleId);

    PageModel<List<RolePO>> getRolesByPage(PageDTO pageDTO);

    PageModel<List<RolePO>> getRolesByQuery(RoleQueryDTO queryDTO);

    boolean edit(RoleEditDTO editDTO);

    boolean save(RoleSaveDTO saveDTO);

    boolean delete(List<Long> ids);

    boolean refreshAdminPrivilege();
}
