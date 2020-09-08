//package com.aero.ops.config;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.convert.MongoConverter;
//
///**
// * 去除_class字段
// **/
//@Configuration
//public class MongoClassConfig implements ApplicationListener<ContextRefreshedEvent>{
//
//    @Autowired
//    @Qualifier("devOpsMongoTemplate")
//    MongoTemplate mongoTemplate;
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//
//        MongoConverter mongoConverter = mongoTemplate.getConverter();
//
//        if (mongoConverter.getTypeMapper().isTypeKey("_class")) {
//            ((MappingMongoConverter) mongoConverter).setTypeMapper(new DefaultMongoTypeMapper(null));
//        }
//    }

//    @Bean
//    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
//        try {
//            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
//        } catch (NoSuchBeanDefinitionException ignore) {
//        }
//
//        // Don't save _class to mongo
//        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
//
//        return mappingConverter;
//    }
//}


