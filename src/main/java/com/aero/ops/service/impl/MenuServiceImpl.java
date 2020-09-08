package com.aero.ops.service.impl;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.constants.MenuType;
import com.aero.ops.entity.dto.MenuEditDTO;
import com.aero.ops.entity.po.MenuPO;
import com.aero.ops.entity.po.RolePO;
import com.aero.ops.entity.vo.MenuVO;
import com.aero.ops.service.IMenuService;
import com.aero.ops.service.IRoleService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author 罗涛
 * @title MenuServiceImpl
 * @date 2020/8/3 18:55
 */

@Service
public class MenuServiceImpl implements IMenuService {
    @Autowired
    @Qualifier("devOpsMongoTemplate")
    MongoTemplate mongoTemplate;

    @Autowired
    IRoleService roleService;

    @Override
    public PageModel<List<MenuPO>> getMenusByPage(PageDTO pageDTO) {
        return getByQuery(new Query(), pageDTO);
    }


//    @Override
//    public PageModel<List<MenuPO>> getMenusByQuery(MenuQueryDTO queryDTO) {
//        Query query = new Query();
//        if(StringUtils.isNotEmpty(queryDTO.getRealName())){
//            Criteria realName = Criteria.where("realName").regex(queryDTO.getRealName());
//            query.addCriteria(realName);
//        }
//        if(Objects.nonNull(queryDTO.getEnable())){
//            Criteria enable = Criteria.where("enable").is(queryDTO.getEnable());
//            query.addCriteria(enable);
//        }
//        if(Objects.nonNull(queryDTO.getRoleId())){
//            Criteria roleId = Criteria.where("roleId").is(queryDTO.getRoleId());
//            query.addCriteria(roleId);
//        }
//        return getByQuery(query, queryDTO);
//    }


    public PageModel<List<MenuPO>> getByQuery(Query query,PageDTO pageDTO){
        Long count = mongoTemplate.count(query, MenuPO.class);
        PageModel<List<MenuPO>> page = new PageModel();
        page.setCount(count.intValue());
        if (count>0) {
            int pageSize = 0;
            int skip = 0;
            if (Objects.nonNull(pageDTO)) {
                pageSize = pageDTO.getPageSize();
                skip = (pageDTO.getPageIndex()-1)*pageSize;
            }else {
                pageSize = Integer.MAX_VALUE;
            }
            query.skip(skip).limit(pageSize);
            List<MenuPO> menus = mongoTemplate.find(query, MenuPO.class);
            page.setData(menus);
        }
        return page;
    }

    @Override
    public List<MenuVO> getMenuTree() {
        return getMenuTreeByQuery(new Query());
    }

    @Override
    public List<MenuVO> getMenuTreeByRole(long roleId) {
        RolePO role = roleService.getRole(roleId);
        List<String> menuList = role.getMenuList();
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").in(menuList);
        criteria.and("menuType").in(Arrays.asList(MenuType.PARENT.getType(),MenuType.NODE_PAGE.getType()));
        query.addCriteria(criteria);
        return getMenuTreeByQuery(query);
    }


    public List<MenuVO> getMenuTreeByQuery(Query query) {
        List<MenuPO> menus = mongoTemplate.find(query, MenuPO.class);;
        List<MenuVO> vos = new ArrayList<>();
        Map<String, List<MenuPO>> subMenus = new HashMap<>();
        for (MenuPO menu : menus) {
            if(StringUtils.isNotEmpty(menu.getParentId())) {
                MenuVO vo = new MenuVO();
                BeanUtils.copyProperties(menu,vo);
                List<MenuPO> menuPOS = subMenus.get(menu.getParentId());
                if(menuPOS==null){
                    menuPOS = new ArrayList<>();
                    menuPOS.add(menu);
                    subMenus.put(menu.getParentId(), menuPOS);
                }else {
                    menuPOS.add(menu);
                }
            }else {
                MenuVO vo = new MenuVO();
                BeanUtils.copyProperties(menu,vo);
                vos.add(vo);
            }
        }
        //填充子项
        for (MenuVO vo : vos){
            vo.setChildren(po2vo(subMenus.get(vo.getId()), subMenus));
        }
        return vos;
    }


    @Override
    public List<MenuVO> getAllParent() {
        Query query = new Query();
        List<Integer> types = Arrays.asList(MenuType.PARENT.getType(), MenuType.NODE_PAGE.getType(), MenuType.HOME_PAGE.getType());
        Criteria criteria = Criteria.where("menuType").in(types);
        query.addCriteria(criteria);
        List<MenuPO> menus = mongoTemplate.find(query, MenuPO.class);;
        List<MenuVO> vos = Collections.emptyList();
        if (!CollectionUtils.isEmpty(menus)) {
            vos = new ArrayList<>();
            for (MenuPO menu : menus) {
                MenuVO vo = new MenuVO();
                BeanUtils.copyProperties(menu,vo);
                vos.add(vo);
            }
        }
        return vos;
    }

    @Override
    public List<String> getAllMenuIds() {
        List<MenuPO> menus = mongoTemplate.find(new Query(), MenuPO.class);
//        StringBuilder sb = new StringBuilder();
        List<String> menuIds = Collections.EMPTY_LIST;
        if(!CollectionUtils.isEmpty(menus)){
            menuIds = new ArrayList<>();
            for (MenuPO menu : menus) {
                menuIds.add(menu.getId());
            }
        }
        return menuIds;
    }


    @Override
    public List<String> getAccessUrls(long roleId) {
        RolePO role = roleService.getRole(roleId);
        List<String> menuList = role.getMenuList();
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").in(menuList);
        query.addCriteria(criteria);
        List<MenuPO> menus = mongoTemplate.find(query, MenuPO.class);
        List<String> urls = Collections.EMPTY_LIST;
        if(!CollectionUtils.isEmpty(menus)){
            urls = new ArrayList<>();
            for (MenuPO menu : menus) {
                if (StringUtils.isNotEmpty(menu.getHref())) {
                    urls.add(menu.getHref());
                }
            }
        }
        return urls;
    }

    @Override
    public boolean save(MenuPO menu) throws Exception {
        Query query = new Query();
        Criteria criteria = Criteria.where("href").is(menu.getHref());
        query.addCriteria(criteria);
        if(mongoTemplate.count(query,MenuPO.class)>0){
            throw new Exception("该节点已添加，请确认！");
        }
        MenuPO save = mongoTemplate.save(menu);
        roleService.refreshAdminPrivilege();
        return Objects.nonNull(save);
    }



    @Override
    public boolean update(MenuEditDTO editDTO) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(editDTO.getId());
        query.addCriteria(criteria);

        Update update = new Update();
        if(StringUtils.isNotEmpty(editDTO.getTitle())){
            update.set("title", editDTO.getTitle());
        }

        if(Objects.nonNull(editDTO.getSpread())){
            update.set("spread", editDTO.getSpread());
        }

        if(Objects.nonNull(editDTO.getMenuType())){
            update.set("menuType", editDTO.getMenuType());
        }

        update.set("icon", editDTO.getIcon());
        update.set("href", editDTO.getHref());
        update.set("parentId", editDTO.getParentId());
        update.set("updTime", new Date());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, MenuPO.class);
        return updateResult.getModifiedCount()>0;
    }

    @Override
    public boolean delete(List<String> mids) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").in(mids);
        query.addCriteria(criteria);
        DeleteResult remove = mongoTemplate.remove(query, MenuPO.class);
        roleService.refreshAdminPrivilege();
        return remove.getDeletedCount()>0;
    }


    @Override
    public MenuPO getMenu(String menuId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(menuId);
        query.addCriteria(criteria);
        MenuPO menuPO = mongoTemplate.findOne(query, MenuPO.class);
        return menuPO;
    }

    public List<MenuVO> po2vo(List<MenuPO> pos,Map<String, List<MenuPO>> subMenus){
        List<MenuVO> vos = null;
        if (!CollectionUtils.isEmpty(pos)) {
            vos = new ArrayList<>();
            for (MenuPO menu : pos) {
                MenuVO vo = new MenuVO();
                BeanUtils.copyProperties(menu,vo);
                if (subMenus.containsKey(menu.getId())) {
                    List<MenuPO> subChildren = subMenus.get(menu.getId());
                    if(!CollectionUtils.isEmpty(subChildren)){
                        vo.setChildren(po2vo(subChildren,subMenus));
                    }
                }
                vos.add(vo);
            }
        }
        return vos;
    }
}
