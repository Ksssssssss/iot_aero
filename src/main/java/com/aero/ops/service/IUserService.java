package com.aero.ops.service;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.UserEditDTO;
import com.aero.ops.entity.dto.UserQueryDTO;
import com.aero.ops.entity.po.UserPO;
import com.aero.ops.entity.vo.UserVO;

import java.util.List;

public interface IUserService {

    boolean exist(String username);

    UserPO getUserByName(String username);

    UserPO getUserByDingId(String dingId);

    PageModel<List<UserVO>> getUsersByPage(PageDTO pageDTO);

    PageModel<List<UserVO>> getUsersByQuery(UserQueryDTO queryDTO);

    boolean updateLastLoginTime(String id);

    boolean save(UserPO user);

    boolean update(UserEditDTO editDTO);

    boolean delete(List<String> uids);
}
