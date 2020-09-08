package com.aero.ops.config;

import lombok.Data;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

/**
 * @author 罗涛
 * @title AbstractMongoConfig
 * @date 2020/7/31 14:42
 */
@Data
public abstract class AbstractMongoConfig {


    private String uri;

    /*
     * Method that creates MongoDbFactory Common to both of the MongoDb
     * connections
     */
    public MongoDbFactory mongoDbFactory() throws Exception {

        return new SimpleMongoClientDbFactory(uri);
    }

    /*
     * Factory method to create the MongoTemplate
     */
    abstract public MongoTemplate getMongoTemplate() throws Exception;

}

