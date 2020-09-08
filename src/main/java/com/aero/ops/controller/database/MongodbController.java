package com.aero.ops.controller.database;

import com.aero.ops.entity.dto.DatabaseQueryDTO;
import com.aero.ops.entity.po.MongoInfoPO;
import com.aero.ops.service.IMongodbService;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/mongoDatabase")
@Api(tags={"Mongodb管理接口"}, value = "MongodbController")
public class MongodbController {
    @Autowired
    IMongodbService mongodbService;

    @RequestMapping(value = "/mongoDatabases", method = RequestMethod.GET)
    public List<String> getDatabases(){
        List<String> databases = mongodbService.getAllDatabases();
        return databases;
    }

    @RequestMapping(value = "/mongoCollections", method = RequestMethod.GET)
    public List<String> getTables(@RequestParam String databaseName){
        MongoInfoPO mongoInfoPO = new MongoInfoPO();
        mongoInfoPO.setDatabase(databaseName);
        List<String> allTables = mongodbService.getAllTables(mongoInfoPO);
        return allTables;
    }

    @RequestMapping(value = "/mongoCols", method = RequestMethod.GET)
    public List<String> getCols(@RequestParam String databaseName,@RequestParam String collectionName){
        if(databaseName == "" || collectionName == "") {
            return new ArrayList<>();
        }
        MongoInfoPO mongoInfoPO = new MongoInfoPO();
        mongoInfoPO.setDatabase(databaseName);
        mongoInfoPO.setCollection(collectionName);

        List<String> allTables = mongodbService.getCols(mongoInfoPO);
        return allTables;
    }

    @RequestMapping(value = "/mongoQuery", method = RequestMethod.POST)
    @ApiOperation(value = "根据查询条件筛选分页数据")
    public String getData(@RequestBody DatabaseQueryDTO queryDTO){
        if(queryDTO.getDatabase() == "" || queryDTO.getDatatable() == "" || queryDTO.getQuery() == "") {
            return ResultUtils.ResultLayUIData(null, 0, "");
        }
        MongoInfoPO mongoInfo = new MongoInfoPO();
        mongoInfo.setDatabase(queryDTO.getDatabase());
        mongoInfo.setCollection(queryDTO.getDatatable());
        mongoInfo.setPageIndex(queryDTO.getPageIndex());
        mongoInfo.setPageSize(queryDTO.getPageSize());
        mongoInfo.setQuery(queryDTO.getQuery());
        return mongodbService.query(mongoInfo);
    }
}
