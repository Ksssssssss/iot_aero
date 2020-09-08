package com.aero.ops.controller.database;

import com.aero.common.model.ResponseModel;
import com.aero.ops.entity.dto.DatabaseQueryDTO;
import com.aero.ops.entity.dto.SqlServerQueryDTO;
import com.aero.ops.entity.po.BaseTablePO;
import com.aero.ops.entity.po.MongoInfoPO;
import com.aero.ops.service.ISqlServerService;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/sqlserver")
@Api(tags={"SqlServer管理接口"}, value = "SqlServerController")
public class SqlServerController {
    @Autowired
    ISqlServerService sqlServerService;


    @RequestMapping(value = "/databases", method = RequestMethod.GET)
    public ResponseModel<List<String>> databases(){
        List<String> databases = sqlServerService.getAllDatabases();
        return ResultUtils.me.success(databases);
    }

    @RequestMapping(value = "/getTables", method = RequestMethod.GET)
    public ResponseModel<List<String>> tables(@RequestParam(value = "database") String database){
        List<BaseTablePO> allTables = sqlServerService.getTables(database);
        List<String> tables = allTables.stream().map(BaseTablePO::getTableName).collect(Collectors.toList());
        return ResultUtils.me.success(tables);
    }

    @RequestMapping(value = "/getColumns", method = RequestMethod.GET)
    public ResponseModel<List<String>> columns(@RequestParam(value = "database") String database, @RequestParam(value = "table") String table){
        List<String> columns = sqlServerService.getColumns(database, table);
        return ResultUtils.me.success(columns);
    }


    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(value = "根据查询条件筛选分页数据")
    public String getData(@RequestBody @Validated SqlServerQueryDTO queryDTO, BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()) {
                log.error("查询参数错误：{}", bindingResult.getAllErrors().get(0).getDefaultMessage());
                return ResultUtils.ResultLayUIData(null, 0, "");
            }
            return sqlServerService.query(queryDTO);
        } catch (Exception e) {
            log.error("查询sqlserver时发生异常：{}", e);
            return ResultUtils.ResultLayUIData(null, 0, "");
        }
    }

}
