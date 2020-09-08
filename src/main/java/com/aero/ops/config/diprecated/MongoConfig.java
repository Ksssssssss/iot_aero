//package com.aero.ops.config;
//
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.convert.CustomConversions;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
//import org.springframework.data.mongodb.core.convert.DbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//
///**
// * @author 罗涛
// * @title MongoConfig
// * @date 2020/6/30 17:58
// */
//
//@Configuration
//public class MongoConfig {
//
//    @Value("${spring.data.mongodb.devops.uri}")
//    private String devOpsUri;
//
//    @Value("${spring.data.mongodb.hardware.uri}")
//    private String hardwareUri;
//
//
//    @Bean
//    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
//        try {
//            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
//        } catch (NoSuchBeanDefinitionException ignore) {
//        }
//        // Don't save _class to mongo
//        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
//
//        return mappingConverter;
//    }
//
//    @Bean(name = "devOpsMongoTemplate")
//    @Primary
//    public MongoTemplate devOpsMongoTemplate() throws Exception {
//        return new MongoTemplate(new SimpleMongoClientDbFactory(devOpsUri));
//    }
//
//    @Bean(name = "hardwareMongoTemplate")
//    public MongoTemplate hardwareMongoTemplate() throws Exception {
//        return new MongoTemplate(new SimpleMongoClientDbFactory(hardwareUri));
//    }
//}
