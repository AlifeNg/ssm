/* 
 *
 * Copyright (C) 1999-2012 IFLYTEK Inc.All Rights Reserved. 
 * 
 * FileName：MongoServiceFactory.java
 * 
 * Description：
 * 
 * History：
 * Version   Author      Date            Operation 
 * 1.0	  baoxu   2015年12月21日下午8:17:06	       Create	
 */
package com.wuying.ssm.util.mongo.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baoxu
 * 
 * @version 1.0
 * 
 */
public class MongoServiceFactory {
    private static Map<String, IMongoService<?>> cacheMap = new HashMap<String, IMongoService<?>>();

    public static synchronized IMongoService<?> getMongoService() {
        // 启用默认的mongoDB配置
        return getMongoService(null);
    }

    public static synchronized IMongoService<?> getMongoService(String dbName) {
        IMongoService<?> mongoService = cacheMap.get(dbName);
        if (mongoService == null) {
            mongoService = new MongoServiceImpl();
            mongoService.setDatasource(dbName);
            cacheMap.put(dbName, mongoService);
        }
        return mongoService;
    }
}
