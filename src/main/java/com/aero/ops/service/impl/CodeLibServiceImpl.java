package com.aero.ops.service.impl;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.CodeLibEditDTO;
import com.aero.ops.entity.dto.CodeLibSaveDTO;
import com.aero.ops.entity.po.CodeLibPO;
import com.aero.ops.entity.po.MenuPO;
import com.aero.ops.service.ICodeLibService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 罗涛
 * @title CodeLibServiceImpl
 * @date 2020/7/16 18:32
 */
@Service
public class CodeLibServiceImpl implements ICodeLibService {

    @Autowired
    @Qualifier("devOpsMongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public PageModel<List<CodeLibPO>> getCodeByPage(PageDTO pageDTO) {
        Query query = new Query();
        Long count = mongoTemplate.count(query, CodeLibPO.class);
        int pageSize = 0;
        int skip = 0;
        if (Objects.nonNull(pageDTO)) {
            pageSize = pageDTO.getPageSize();
            skip = (pageDTO.getPageIndex()-1)*pageSize;
        }else {
            pageSize = Integer.MAX_VALUE;
        }
        query.skip(skip).limit(pageSize);
        List<CodeLibPO> codes = mongoTemplate.find(query, CodeLibPO.class);
        PageModel page = new PageModel();
        page.setCount(count.intValue());
        page.setData(codes);
        return page;
    }

    @Override
    public Boolean save(CodeLibSaveDTO saveDTO) {
        try {
            mongoTemplate.save(saveDTO);
            return true;
        }catch (Exception e){
            throw e;
        }
    }


    @Override
    public boolean update(CodeLibEditDTO editDTO) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(editDTO.getId());
        query.addCriteria(criteria);

        Update update = new Update();
        if(StringUtils.isNotEmpty(editDTO.getProjectName())){
            update.set("projectName", editDTO.getProjectName());
        }

        if(StringUtils.isNotEmpty(editDTO.getCloneType())){
            update.set("cloneType", editDTO.getCloneType());
        }

        if(StringUtils.isNotEmpty(editDTO.getVersionControl())){
            update.set("versionControl", editDTO.getVersionControl());
        }

        if(StringUtils.isNotEmpty(editDTO.getCompiler())){
            update.set("compiler", editDTO.getCompiler());
        }

        if(StringUtils.isNotEmpty(editDTO.getUrl())){
            update.set("url", editDTO.getUrl());
        }

        update.set("updTime", new Date());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, CodeLibPO.class);
        return updateResult.getModifiedCount()>0;
    }

    @Override
    public boolean delete(List<String> codeIds) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").in(codeIds);
        query.addCriteria(criteria);
        DeleteResult remove = mongoTemplate.remove(query, CodeLibPO.class);
        return remove.getDeletedCount()>0;
    }

    @Override
    public CodeLibPO getCodeLib(String id) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(id);
        query.addCriteria(criteria);
        CodeLibPO codeLibPO = mongoTemplate.findOne(query, CodeLibPO.class);
        return codeLibPO;
    }


    @Override
    public CodeLibPO getCodeLibByName(String projectName) {
        Query query = new Query();
        Criteria criteria = Criteria.where("projectName").is(projectName);
        query.addCriteria(criteria);
        CodeLibPO codeLibPO = mongoTemplate.findOne(query, CodeLibPO.class);
        return codeLibPO;
    }
}
