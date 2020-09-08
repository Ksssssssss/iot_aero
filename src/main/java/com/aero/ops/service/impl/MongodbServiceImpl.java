package com.aero.ops.service.impl;

import com.aero.ops.entity.po.MongoInfoPO;
import com.aero.ops.service.IMongodbService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongodbService
 */
@Service
public class MongodbServiceImpl implements IMongodbService {
    @Autowired
    MongoClient mongoClient;

    public List<String> getAllDatabases() {
        try {
            MongoIterable<String> databaseNames = mongoClient.listDatabaseNames();
            List<String> databases = new ArrayList();
            for (String document: databaseNames) {
                databases.add(document);
            }
            return databases;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAllTables(MongoInfoPO mongoInfoPO) {
        try {
            MongoDatabase db = mongoClient.getDatabase(mongoInfoPO.getDatabase());
            MongoIterable<String> mongoIterable = db.listCollectionNames();
            List<String> tables = new ArrayList();
            for (String document: mongoIterable) {
                tables.add(document);
            }
            return tables;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getCols(MongoInfoPO mongoInfoPO){
        MongoDatabase db = mongoClient.getDatabase(mongoInfoPO.getDatabase());
        MongoCollection<Document> coll = db.getCollection(mongoInfoPO.getCollection());
        Document document = coll.find().first();
        if(document==null) return new ArrayList<>();
        List<String> keys = new ArrayList();
        for( String key: document.keySet()){
            keys.add(key);
        }
        return keys;
    }

    public String query(MongoInfoPO mongoInfoPO) {
        String database = mongoInfoPO.getDatabase();
        String collection = mongoInfoPO.getCollection();
        String query = mongoInfoPO.getQuery();
        Integer pageIndex = Integer.valueOf(mongoInfoPO.getPageIndex());
        Integer pageSize = Integer.valueOf(mongoInfoPO.getPageSize());
        int firstCurves = query.indexOf("(");
        int lastCurves = query.lastIndexOf(");");
        String bson = query.substring(firstCurves + 1, lastCurves);
        BsonDocument queryObj = BsonDocument.parse(bson);
        MongoDatabase db = mongoClient.getDatabase(database);
        MongoCollection<Document> coll = db.getCollection(collection);
        long count = coll.countDocuments(queryObj);
        FindIterable<Document> findIterable = coll.find(queryObj).skip(pageSize * (pageIndex - 1)).limit(pageSize);
        List<Document> result = new ArrayList();
        for (Document document:findIterable) {
            result.add(document);
        }

        //按照layui的返回格式，传参
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("count", count);
        map.put("data", result);
        map.put("msg", "");
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        String json = JSON.toJSONStringWithDateFormat(map,dateFormat, SerializerFeature.WriteDateUseDateFormat);
        return json;
    }
}
