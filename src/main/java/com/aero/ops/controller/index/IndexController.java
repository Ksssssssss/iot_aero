package com.aero.ops.controller.index;

import com.aero.ops.entity.po.BaseTablePO;
import com.aero.ops.service.ISqlServerService;
import com.mongodb.MongoClient;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 罗涛
 * @title DeviceController
 * @date 2020/7/2 10:01
 */
@RestController
@RequestMapping(value = "/demo")
@Api(tags={"测试接口"}, value = "IndexController")
public class IndexController {
    @Autowired
    ISqlServerService sqlServerService;

    @Autowired
    MongoClient mongoClient;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(){

         mongoClient.getDatabase("Device");
        return "Hello, BCHD Ops!";
    }

    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public List<String> tables(){
        List<BaseTablePO> allTables = sqlServerService.getAllTables();
        List<String> tables = allTables.stream().map(BaseTablePO::getTableName).collect(Collectors.toList());
        return tables;
    }


    @RequestMapping(value = "/tablesByDatabase", method = RequestMethod.GET)
    public List<String> tables(@RequestParam(value = "database") String database){
        List<BaseTablePO> allTables = sqlServerService.getTables(database);
        List<String> tables = allTables.stream().map(BaseTablePO::getTableName).collect(Collectors.toList());
        return tables;
    }


    @RequestMapping(value = "/getDatabases", method = RequestMethod.GET)
    public List<String> databases(){
        List<String> databases = sqlServerService.getAllDatabases();
        return databases;
    }


}