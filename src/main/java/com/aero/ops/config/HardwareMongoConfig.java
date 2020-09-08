package com.aero.ops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author 罗涛
 * @title DevOpsMongoConfig
 * @date 2020/7/31 14:44
 */

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.hardware")
public class HardwareMongoConfig extends AbstractMongoConfig {

    @Override
    @Bean(name = "hardwareMongoTemplate")
    public MongoTemplate getMongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
