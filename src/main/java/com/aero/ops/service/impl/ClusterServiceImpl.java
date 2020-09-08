package com.aero.ops.service.impl;

import com.aero.ops.constants.ClusterType;
import com.aero.ops.entity.dto.ClusterEditDTO;
import com.aero.ops.entity.dto.ClusterSaveDTO;
import com.aero.ops.entity.po.ClusterPO;
import com.aero.ops.service.IClusterService;
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

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 罗涛
 * @title ClusterServiceImpl
 * @date 2020/8/11 11:41
 */
@Service
public class ClusterServiceImpl implements IClusterService {

    @Autowired
    @Qualifier("devOpsMongoTemplate")
    MongoTemplate mongoTemplate;


    @Override
    public List<ClusterPO> getAll() {
        List<ClusterPO> all = mongoTemplate.findAll(ClusterPO.class);
        return all;
    }

    @Override
    public List<ClusterPO> getAllCluster() {
        Query query = new Query();
        Criteria criteria = Criteria.where("clusterType").is(ClusterType.CLUSTER.getType());
        query.addCriteria(criteria);
        List<ClusterPO> allCluster = mongoTemplate.find(query, ClusterPO.class);
        return allCluster;
    }

    @Override
    public ClusterPO save(ClusterSaveDTO saveDTO) {
        ClusterPO po = new ClusterPO();
        BeanUtils.copyProperties(saveDTO,po);
        po.setCrtTime(new Date());
        po.setUpdTime(new Date());
        ClusterPO save = mongoTemplate.save(po);
        return save;
    }

    @Override
    public ClusterPO getById(String id) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(id);
        query.addCriteria(criteria);
        ClusterPO one = mongoTemplate.findOne(query, ClusterPO.class);
        return one;
    }

    @Override
    public boolean delete(List<String> cids) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").in(cids);
        query.addCriteria(criteria);
        DeleteResult remove = mongoTemplate.remove(query, ClusterPO.class);
        return remove.getDeletedCount()>0;
    }


    @Override
    public boolean edit(ClusterEditDTO editDTO) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(editDTO.getId());
        query.addCriteria(criteria);

        Update update = new Update();
        if(StringUtils.isNotEmpty(editDTO.getTitle())){
            update.set("title", editDTO.getTitle());
        }

        if(Objects.nonNull(editDTO.getClusterType())){
            update.set("clusterType", editDTO.getClusterType());
        }

        if(StringUtils.isNotEmpty(editDTO.getPid())){
            update.set("pid", editDTO.getPid());
        }

        if(StringUtils.isNotEmpty(editDTO.getUrl())){
            update.set("url", editDTO.getUrl());
        }

        update.set("updTime", new Date());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ClusterPO.class);
        return updateResult.getModifiedCount()>0;
    }

    @Override
    public boolean batchStatusUpdate(List<String> urls, boolean pingAccess) {
        Query query = new Query();
        Criteria criteria = Criteria.where("url").in(urls);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set("status", pingAccess);
        update.set("updTime", new Date());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ClusterPO.class);

        Query reverseQuery = new Query();
        Criteria reverseCriteria = Criteria.where("url").nin(urls);
        reverseQuery.addCriteria(reverseCriteria);

        Update reverseUpdate = new Update();
        reverseUpdate.set("status", !pingAccess);
        reverseUpdate.set("updTime", new Date());
        UpdateResult reverseUpdateResult = mongoTemplate.updateMulti(reverseQuery, reverseUpdate, ClusterPO.class);
        return (reverseUpdateResult.getModifiedCount() + updateResult.getModifiedCount())>0;
    }
}
