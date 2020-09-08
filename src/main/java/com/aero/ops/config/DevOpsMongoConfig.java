package com.aero.ops.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * @author 罗涛
 * @title DevOpsMongoConfig
 * @date 2020/7/31 14:44
 */

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.devops")
public class DevOpsMongoConfig extends AbstractMongoConfig {

    @Autowired
    MongoMappingContext context;

    @Autowired
    BeanFactory beanFactory;

    @Override
    @Primary
    @Bean(name = "devOpsMongoTemplate")
    public MongoTemplate getMongoTemplate() throws Exception {
//        MappingMongoConverter converter =
//                new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
//        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
//        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

        MongoDbFactory factory = mongoDbFactory();
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
        }

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), mappingConverter);
        return mongoTemplate;
    }
}
