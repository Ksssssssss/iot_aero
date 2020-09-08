package com.aero.ops.controller.monitor;

import com.aero.ops.service.IMonitorService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/monitor")
@Api(tags={"SystemInfo管理接口"}, value = "SystemInfoController")
public class SystemInfoController {
    @Autowired
    IMonitorService monitorService;

    @RequestMapping(value = "/serverList", method = RequestMethod.GET)
    public List<Map>  getServerList(){
        List<String> allIndices = monitorService.getAllIndices();
        List<Map> list = new ArrayList<>();
        for (String indices:allIndices) {
            if(indices.contains("metricbeat")) {
                Map<String,Object> systemInfos = new HashMap<>();
                systemInfos.put("memory",monitorService.getSystemMemory(indices));
                systemInfos.put("cpu",monitorService.getSystemCPU(indices));
                systemInfos.put("tcp",monitorService.getSystemTCP(indices));
                list.add(systemInfos);
            }
        }
        return list;
    }
}
