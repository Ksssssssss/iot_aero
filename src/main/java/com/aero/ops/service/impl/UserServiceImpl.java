package com.aero.ops.service.impl;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.constants.DefaultRoles;
import com.aero.ops.entity.dto.UserEditDTO;
import com.aero.ops.entity.dto.UserQueryDTO;
import com.aero.ops.entity.po.UserPO;
import com.aero.ops.entity.vo.UserVO;
import com.aero.ops.service.IRoleService;
import com.aero.ops.service.IUserService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    @Qualifier("devOpsMongoTemplate")
    MongoTemplate mongoTemplate;

    @Autowired
    IRoleService roleService;

    @Override
    public boolean exist(String username) {
        Query query = new Query();
        Criteria where = Criteria.where("realName").is(username);
        where.and("roleId").is(DefaultRoles.ADMIN.getRoleId());
        query.addCriteria(where);
        List<UserPO> userPOS = mongoTemplate.find(query, UserPO.class);
        return !CollectionUtils.isEmpty(userPOS);
    }


    @Override
    public UserPO getUserByName(String username) {
        Query query = new Query();
        Criteria where = Criteria.where("realName").is(username);
        where.and("roleId").is(DefaultRoles.ADMIN.getRoleId());
        query.addCriteria(where);
        List<UserPO> userPOS = mongoTemplate.find(query, UserPO.class);
        if(CollectionUtils.isEmpty(userPOS)){
            return null;
        }else {
            if(userPOS.size()>1){
                log.warn("realName为{}的管理员超过1个，请确认！",username);
            }
            return userPOS.get(0);
        }
    }

    @Override
    public UserPO getUserByDingId(String dingId) {
        Query query = new Query();
        Criteria where = Criteria.where("dingTalkUid").is(dingId);
        query.addCriteria(where);
        List<UserPO> userPOS = mongoTemplate.find(query, UserPO.class);
        if(CollectionUtils.isEmpty(userPOS)){
            return null;
        }else {
            if(userPOS.size()>1){
                log.warn("dingId = {}对应的用户超过1个，请确认！",dingId);
            }
            return userPOS.get(0);
        }
    }

    @Override
    public PageModel<List<UserVO>> getUsersByPage(PageDTO pageDTO) {
        return getByQuery(new Query(), pageDTO);
    }


    @Override
    public PageModel<List<UserVO>> getUsersByQuery(UserQueryDTO queryDTO) {
        Query query = new Query();
        if(StringUtils.isNotEmpty(queryDTO.getRealName())){
            Criteria realName = Criteria.where("realName").regex(queryDTO.getRealName());
            query.addCriteria(realName);
        }
        if(Objects.nonNull(queryDTO.getEnable())){
            Criteria enable = Criteria.where("enable").is(queryDTO.getEnable());
            query.addCriteria(enable);
        }
        if(Objects.nonNull(queryDTO.getRoleId())){
            Criteria roleId = Criteria.where("roleId").is(queryDTO.getRoleId());
            query.addCriteria(roleId);
        }
        return getByQuery(query, queryDTO);
    }


    public PageModel<List<UserVO>> getByQuery(Query query,PageDTO pageDTO){
        Long count = mongoTemplate.count(query, UserPO.class);
        PageModel<List<UserVO>> page = new PageModel();
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
            List<UserPO> userPOS = mongoTemplate.find(query, UserPO.class);
            List<UserVO> vos = new ArrayList<>();
            for(UserPO po:userPOS){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(po,userVO);
                userVO.setRoleName(roleService.getRole(po.getRoleId()).getRoleName());
                vos.add(userVO);
            }
            page.setData(vos);
        }
        return page;
    }

    @Override
    public boolean updateLastLoginTime(String id) {
        try {
            Query query = new Query();
            Criteria uid = Criteria.where("_id").is(id);
            query.addCriteria(uid);
            Update update = new Update();
            update.set("lastLoginTime",new Date());
            mongoTemplate.updateFirst(query, update, UserPO.class);
            return true;
        } catch (Exception e) {
            log.error("更新最后登录时间时发生异常：{0}", e);
            return false;
        }
    }

    @Override
    public boolean save(UserPO user) {
        if (Objects.isNull(user.getCrtTime())) {
            user.setCrtTime(new Date());
        }
        UserPO save = mongoTemplate.save(user);
        return Objects.nonNull(save);
    }

    @Override
    public boolean update(UserEditDTO editDTO) {
        Query query = new Query();
        Criteria id = Criteria.where("_id").is(editDTO.getId());
        query.addCriteria(id);
        Update update = new Update();
        update.set("updTime", new Date());
        if(Objects.nonNull(editDTO.getEnable())){
            update.set("enable", editDTO.getEnable());
        }
        if(Objects.nonNull(editDTO.getRoleId())){
            update.set("roleId", editDTO.getRoleId());
        }
        if(StringUtils.isNotEmpty(editDTO.getDingNickName())){
            update.set("dingNickName", editDTO.getDingNickName());
        }
        if(StringUtils.isNotEmpty(editDTO.getRealName())){
            update.set("realName", editDTO.getRealName());
        }
        if(StringUtils.isNotEmpty(editDTO.getDingTalkUid())){
            update.set("dingTalkUid", editDTO.getDingTalkUid());
        }
        if(StringUtils.isNotEmpty(editDTO.getPhoneNumber())){
            update.set("phoneNumber", editDTO.getPhoneNumber());
        }
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, UserPO.class);
        return updateResult.getModifiedCount()>0;
    }

    @Override
    public boolean delete(List<String> uids) {
        Query query = new Query();
        Criteria id = Criteria.where("_id").in(uids);
        query.addCriteria(id);
        DeleteResult remove = mongoTemplate.remove(query, UserPO.class);
        return remove.getDeletedCount()>0;
    }
}
