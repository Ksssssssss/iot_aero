package com.aero.ops.service.impl;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.constants.DefaultRoles;
import com.aero.ops.entity.dto.RoleEditDTO;
import com.aero.ops.entity.dto.RoleQueryDTO;
import com.aero.ops.entity.dto.RoleSaveDTO;
import com.aero.ops.entity.po.RolePO;
import com.aero.ops.entity.po.ServerPO;
import com.aero.ops.entity.vo.UserVO;
import com.aero.ops.service.IMenuService;
import com.aero.ops.service.IRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    @Qualifier("devOpsMongoTemplate")
    MongoTemplate mongoTemplate;

    @Autowired
    IMenuService menuService;


    @Override
    public RolePO getRole(long roleId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(roleId);
        query.addCriteria(criteria);
        RolePO rolePO = mongoTemplate.findOne(query, RolePO.class);
        return rolePO;
    }

    @Override
    public PageModel<List<RolePO>> getRolesByPage(PageDTO pageDTO) {
        Query query = new Query();
        return getByQuery(query,pageDTO);
    }


    @Override
    public PageModel<List<RolePO>> getRolesByQuery(RoleQueryDTO queryDTO) {
        Query query = new Query();
        if(StringUtils.isNotEmpty(queryDTO.getRoleName())){
            Criteria roleName = Criteria.where("roleName").regex(queryDTO.getRoleName());
            query.addCriteria(roleName);
        }
        if(Objects.nonNull(queryDTO.getEnable())){
            Criteria criteria = Criteria.where("enable").is(queryDTO.getEnable());
            query.addCriteria(criteria);
        }
        return getByQuery(query,queryDTO);
    }

    public PageModel<List<RolePO>> getByQuery(Query query, PageDTO pageDTO){
        Long count = mongoTemplate.count(query, RolePO.class);
        PageModel<List<RolePO>> page = new PageModel();
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
            List<RolePO> roles = mongoTemplate.find(query, RolePO.class);
            page.setData(roles);
        }
        return page;
    }


    @Override
    public boolean edit(RoleEditDTO editDTO) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(editDTO.getId());
        query.addCriteria(criteria);

        Update update = new Update();
        if(StringUtils.isNotEmpty(editDTO.getRoleName())){
            update.set("roleName", editDTO.getRoleName());
        }

        if(StringUtils.isNotEmpty(editDTO.getRemark())){
            update.set("remark", editDTO.getRemark());
        }

        if(Objects.nonNull(editDTO.getEnable())){
            update.set("enable", editDTO.getEnable());
        }
        update.set("menuList", editDTO.getMenuList());
        update.set("updTime", new Date());
        mongoTemplate.updateFirst(query,update,RolePO.class);
        return true;
    }


    @Override
    public boolean save(RoleSaveDTO saveDTO) {
        RolePO role = new RolePO();
        BeanUtils.copyProperties(saveDTO, role);
        role.setId(getMaxId()+1);
        role.setCrtTime(new Date());
        role.setUpdTime(new Date());
        mongoTemplate.save(role);
        return true;
    }


    @Override
    public boolean delete(List<Long> ids) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").in(ids);
        query.addCriteria(criteria);
        mongoTemplate.remove(query,RolePO.class);
        return true;
    }


    public synchronized long getMaxId(){
        Query query = new Query();
        Sort.Order id = Sort.Order.desc("_id");
        query.with(Sort.by(id));
        RolePO one = mongoTemplate.findOne(query, RolePO.class);
        if(Objects.nonNull(one)){
            return one.getId();
        }
        return 0;
    }

    @Override
    public boolean refreshAdminPrivilege() {
        RoleEditDTO role = new RoleEditDTO();
        role.setId((long) DefaultRoles.ADMIN.getRoleId());
        role.setMenuList(menuService.getAllMenuIds());
        return edit(role);
    }
}
