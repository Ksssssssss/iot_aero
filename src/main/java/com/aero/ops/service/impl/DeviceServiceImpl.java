package com.aero.ops.service.impl;


import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dao.DevicePOMapper;
import com.aero.ops.entity.dto.DeviceEditDTO;
import com.aero.ops.entity.dto.DeviceQueryDTO;
import com.aero.ops.entity.po.DevicePO;
import com.aero.ops.entity.po.DevicePOExample;
import com.aero.ops.entity.po.DevicePOWithBLOBs;
import com.aero.ops.service.IDeviceService;
import com.alibaba.druid.sql.builder.SQLSelectBuilder;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 罗涛
 * @title DeviceServiceImpl
 * @date 2020/6/28 19:27
 */
@Slf4j
@Service
public class DeviceServiceImpl implements IDeviceService {

    @Autowired
    DevicePOMapper devicePOMapper;

    @Autowired
    SqlSessionFactory factory;


    @Override
    public PageModel<List<DevicePO>> getDefaultDevices(PageDTO pageDTO) {
        //再从sqlserver查找
        DevicePOExample example = new DevicePOExample();
        DevicePOExample.Criteria criteria = example.createCriteria();
        criteria.andVisibleEqualTo(1);
        Long count = devicePOMapper.countByExample(example);
        Map<String, Integer> data = new HashMap();
        int pageNumber = pageDTO.getPageIndex();
        int pageSize = pageDTO.getPageSize();
        data.put("pageIndex", pageNumber);
        data.put("pageSize", pageSize);
        List<DevicePO> devicePOS = devicePOMapper.queryByPage(data);
        PageModel page = new PageModel();
        page.setCount(count.intValue());
        page.setData(devicePOS);
        return page;
    }


    @Override
    public PageModel<List<DevicePO>> getDevicesByQuery(DeviceQueryDTO queryDTO) {
        DevicePOExample example = new DevicePOExample();
        DevicePOExample.Criteria criteria = example.createCriteria();
        criteria.andVisibleEqualTo(1);
        if (Objects.nonNull(queryDTO.getStatus())) {
            int status = queryDTO.getStatus();
            if (status == 1) {
                criteria.andStatusEqualTo("在线");
            } else if (status == 2) {
                criteria.andStatusEqualTo("离线");
            }
        }
        if (Objects.nonNull(queryDTO.getSensorType())){
            int sensorType = queryDTO.getSensorType();
            criteria.andSensorTypeEqualTo(String.valueOf(sensorType));
        }
        if(StringUtils.isNotEmpty(queryDTO.getMac())){
            String mac = queryDTO.getMac();
            criteria.andMacLike("%" + mac + "%");
        }
        if(StringUtils.isNotEmpty(queryDTO.getSensorName())){
            String sensorName = queryDTO.getSensorName();
            criteria.andSensorNameLike("%" + sensorName + "%");
        }
        int pageIndex = queryDTO.getPageIndex();
        int pageSize = queryDTO.getPageSize();
        int start = (pageIndex-1)*pageSize;
        int end = pageIndex * pageSize;
        int offset = start+1;
        Long count = devicePOMapper.countByExample(example);
//        List<DevicePO> devicePOS = devicePOMapper.selectByExampleWithPage(example, offset, end);
        String sql = factory.getConfiguration().getMappedStatement("com.aero.ops.entity.dao.DevicePOMapper.selectByExample").getBoundSql(example).getSql();
        sql = StringUtils.replace(sql, "\n"," ");
        String fullSql = buildFullSql(sql, criteria);
        String selectFields = StringUtils.substringBetween(fullSql.toLowerCase(), "select ", " from");
        int fieldsIndex = StringUtils.indexOf(fullSql.toLowerCase(), selectFields);
        String srcFields = StringUtils.substring(fullSql, fieldsIndex, fieldsIndex+selectFields.length());
        String newFields = StringUtils.join(srcFields, ", ROW_NUMBER() OVER(ORDER BY ID) AS RowId");
        fullSql = StringUtils.replace(fullSql, srcFields, newFields);
        List<DevicePO> devicePOS = devicePOMapper.selectByExampleWithPage(fullSql, offset, end);
        PageModel page = new PageModel();
        //这种分页性能很低，后续需要优化 TODO
//        List<DevicePO> devicePOS = devicePOMapper.selectByExample(example);
//        int size = 0;
//        if(!CollectionUtils.isEmpty(devicePOS)){
//            size = devicePOS.size();
//            boolean lastPage = size/pageSize<=pageIndex;
//            end = lastPage? Math.min(end, size) :end;
//            page.setData(devicePOS.subList(start,end));
//        }
//        page.setCount(size);

        page.setCount(count.intValue());
        page.setData(devicePOS);
        return page;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(DeviceEditDTO editDTO) {
        DevicePOWithBLOBs devicePOWithBLOBs = new DevicePOWithBLOBs();
        BeanUtils.copyProperties(editDTO, devicePOWithBLOBs);
        if(StringUtils.isEmpty(editDTO.getFuncs())){
            devicePOWithBLOBs.setFuncs(null);
        }
        devicePOMapper.updateByPrimaryKeySelective(devicePOWithBLOBs);
        return true;
    }


    public String buildFullSql(String sql, DevicePOExample.Criteria criteria){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<sql.length();i++){
            char c = sql.charAt(i);
            if(c!='?'){
                sb.append(c);
            }else {
                int keyIndex = 0;
                int spaceTime = 0;
                for(int j=i;j>=0;j--){
                    char ct = sql.charAt(j);
                    if(ct==' '){
                        if(spaceTime==2){
                            keyIndex = j;
                            break;
                        }
                        spaceTime++;
                    }
                }
                String key = StringUtils.substring(sql, keyIndex+1, i - 1);
                for(DevicePOExample.Criterion criterion : criteria.getAllCriteria()){
                    if(criterion.getCondition().equalsIgnoreCase(key)){
                        Object value = criterion.getValue();
                        if(value instanceof String){
                            sb.append('\'');
                            sb.append(value);
                            sb.append('\'');
                        }else {
                            sb.append(value);
                        }
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }
}
