package com.aero.ops.service;

import com.aero.ops.entity.po.MongoInfoPO;

import java.util.List;

public interface IMongodbService {
    List<String> getAllDatabases();
    List<String> getAllTables(MongoInfoPO mongoInfoPO);
    List<String> getCols(MongoInfoPO mongoInfoPO);
    String query(MongoInfoPO mongoInfoPO);
}
