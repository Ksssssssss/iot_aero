package com.aero.ops.service.impl;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.DeviceQueryDTO;
import com.aero.ops.entity.dto.MongoDeviceEditDTO;
import com.aero.ops.entity.po.MongoDevicePO;
import com.aero.ops.service.IMongoDeviceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author 罗涛
 * @title MongoDeviceServiceImpl
 * @date 2020/7/31 14:03
 */

@Service
public class MongoDeviceServiceImpl implements IMongoDeviceService {
    @Autowired
    @Qualifier("hardwareMongoTemplate")
    MongoTemplate hardwareMongoTemplate;

    @Override
    public PageModel<List<MongoDevicePO>> getDefaultDevices(PageDTO pageDTO) {
        return null;
    }

    @Override
    public PageModel<List<MongoDevicePO>> getDevicesByQuery(DeviceQueryDTO queryDTO) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(queryDTO.getMac())) {
            Criteria criteria = Criteria.where("MAC").is(queryDTO.getMac());
            query.addCriteria(criteria);
        }
        if (StringUtils.isNotEmpty(queryDTO.getSensorName())) {
            Criteria criteria = Criteria.where("SENSOR_NAME").regex(queryDTO.getSensorName());
            query.addCriteria(criteria);
        }
        if (Objects.nonNull(queryDTO.getSensorType())) {
            Criteria criteria = Criteria.where("SENSOR_TYPE").is(String.valueOf(queryDTO.getSensorType()));
            query.addCriteria(criteria);
        }
        if (Objects.nonNull(queryDTO.getStatus())) {
            int state = queryDTO.getStatus();
            if (state==1) {
                Criteria criteria = Criteria.where("STATUS").is("在线");
                query.addCriteria(criteria);
            }else if(state==2){
                Criteria criteria = Criteria.where("STATUS").is("离线");
                query.addCriteria(criteria);
            }
        }

        Long count = hardwareMongoTemplate.count(query, MongoDevicePO.class);
        int pageIndex = queryDTO.getPageIndex();
        int pageSize = queryDTO.getPageSize();
        int start = (pageIndex-1)*pageSize;
        query.skip(start).limit(pageSize);

        List<MongoDevicePO> devices = hardwareMongoTemplate.find(query, MongoDevicePO.class);
        PageModel page = new PageModel();
        page.setCount(count.intValue());
        page.setData(devices);
        return page;
    }

    @Override
    public boolean edit(MongoDeviceEditDTO editDTO) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(editDTO.get_id());
        query.addCriteria(criteria);

        Update update = new Update();
        if(StringUtils.isNotEmpty(editDTO.getFuncs())){
            update.set("Funcs",editDTO.getFuncs());
        }

        if(StringUtils.isNotEmpty(editDTO.getSensorName())){
            update.set("SENSOR_NAME",editDTO.getSensorName());
        }

        if(Objects.nonNull(editDTO.getSecondchecktime())){
            update.set("SecondCheckTime",editDTO.getSecondchecktime());
        }

        if(StringUtils.isNotEmpty(editDTO.getSerialnum())){
            update.set("SerialNum",editDTO.getSerialnum());
        }

        if(StringUtils.isNotEmpty(editDTO.getStartvalue())){
            update.set("StartValue",editDTO.getStartvalue());
        }
        hardwareMongoTemplate.updateMulti(query,update,MongoDevicePO.class);
        return true;
    }
}
