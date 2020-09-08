package com.aero.ops.service;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.MenuEditDTO;
import com.aero.ops.entity.po.MenuPO;
import com.aero.ops.entity.vo.MenuVO;

import java.util.List;

/**
 * @author 罗涛
 * @title IMenuService
 * @date 2020/8/3 18:53
 */
public interface IMenuService {
    PageModel<List<MenuPO>> getMenusByPage(PageDTO pageDTO);

    boolean save(MenuPO menu) throws Exception ;

    boolean update(MenuEditDTO editDTO);

    boolean delete(List<String> mids);

    List<MenuVO> getMenuTree();

    List<MenuVO> getMenuTreeByRole(long roleId);

    List<String> getAllMenuIds();

    List<String> getAccessUrls(long roleId);

    List<MenuVO> getAllParent();

    MenuPO getMenu(String menuId);
}
