package com.aero.ops.service.impl;

import com.aero.ops.entity.dao.CommonMapper;
import com.aero.ops.entity.dto.SqlServerQueryDTO;
import com.aero.ops.entity.po.BaseTablePO;
import com.aero.ops.service.ISqlServerService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlServerService
 *
 * @Author: taomee
 * @Date: 2020/7/4 0004 13:25
 * @Description:
 */
@Slf4j
@Service
public class SqlServerServiceImpl implements ISqlServerService {
    @Autowired
    CommonMapper commonMapper;

    @Autowired
    DataSource dataSource;

    @Override
    public List<BaseTablePO> getAllTables() {
        try {
            return commonMapper.showTables();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<BaseTablePO> getTables(String database) {
        try {
            String schemaTable = StringUtils.joinWith(".", database, "INFORMATION_SCHEMA.TABLES");
            return commonMapper.getTables(schemaTable,database);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<String> getAllDatabases() {
        try {
            List<String> databases = commonMapper.showDatabases();
            return databases;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<String> getColumns(String database, String table) {
        List<String> columns = commonMapper.getColumns(database,table);
        return columns;
    }


    @Override
    public String query(SqlServerQueryDTO queryDTO) throws Exception {
        String sql = queryDTO.getQuery();
        int pageIndex = queryDTO.getPageIndex();
        int pageSize = queryDTO.getPageSize();
        String orderField = queryDTO.getOrderField();

        long count = 0L;
        String lowerSql = sql.toLowerCase();
        String selectFields = StringUtils.substringBetween(lowerSql, "select ", " from");
        int fieldsIndex = StringUtils.indexOf(lowerSql, selectFields);
        String srcFields = StringUtils.substring(sql, fieldsIndex, fieldsIndex + selectFields.length());
        String countSql = StringUtils.replace(sql, srcFields, "count(0)");
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement countPs = connection.prepareStatement(countSql);
            ResultSet countRs = countPs.executeQuery();
            if(countRs.next()){
                Object obj = countRs.getObject(1);
                count = Long.parseLong(obj.toString());
            }

            if(StringUtils.countMatches(lowerSql, "top(")>0){
                String countStr = StringUtils.substringBetween(lowerSql, "top(", ")");
                long countTemp = Integer.parseInt(countStr.trim());
                count = Math.min(count, countTemp);
            }

            if(StringUtils.countMatches(sql, ";")>0){
                sql = StringUtils.replace(sql, ";", "");
            }
//            select top 10 * from [HardwarePlatform].[dbo].SENSOR where ID not in (Select top (0) ID from [HardwarePlatform].[dbo].SENSOR order by ID) order by ID
            sql = StringUtils.replace(sql, srcFields, StringUtils.join(srcFields, ", ROW_NUMBER() OVER(ORDER BY ", orderField, ") AS RowId"));
            String pageSql = StringUtils.join("select * from (", sql, ") as b where RowId between ", ((pageIndex-1)*pageSize+1), " and ", pageIndex*pageSize);
//            log.info("pageSql = {}", pageSql);
            PreparedStatement preparedStatement = connection.prepareStatement(pageSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            // 获得列的结果
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<String> colNameList=new ArrayList<String>();
            // 获取总的列数
            int columnSize = metaData.getColumnCount();
            for (int i = 0; i < columnSize; i++) {
                // 获取第 i列的字段名称
                String colName = metaData.getColumnName(i + 1);
                String colType = metaData.getColumnTypeName(i+1);//类型
                colNameList.add(metaData.getColumnName(i+1));
            }
            while (resultSet.next()) {
                Map<String, Object> map=new HashMap<>();
                for(int i=0;i<columnSize;i++){
                    String key=colNameList.get(i);
                    Object value=resultSet.getString(colNameList.get(i));
                    map.put(key, value);
                }
                results.add(map);
            }
            //按照layui的返回格式，传参
            Map<String, Object> map = new HashMap<>();
            map.put("code", 0);
            map.put("count", count);
            map.put("data", results);
            map.put("msg", "");
            String dateFormat = "yyyy-MM-dd HH:mm:ss";
            String json = JSON.toJSONStringWithDateFormat(map,dateFormat, SerializerFeature.WriteDateUseDateFormat);
            return json;
        } finally {
            connection.close();
        }
    }
}
