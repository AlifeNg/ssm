/* 
 *
 * Copyright (C) 1999-2012 IFLYTEK Inc.All Rights Reserved. 
 * 
 * FileName：DataCatchClient.java
 * 
 * Description：
 * 
 * History：
 * Version   Author      Date            Operation 
 * 1.0	  baoxu   2015年3月31日下午4:55:33	       Create	
 */
package com.yunjun.auto.datacache.common;

import com.yunjun.auto.common.utils.JsonUtil;
import com.yunjun.auto.datacache.common.redis.RedisClient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author baoxu
 * 
 * @version 1.0
 * 
 */
public class DataCacheClient {

    private static final Logger logger = LoggerFactory
            .getLogger(DataCacheClient.class);

    // private static Map<String, CacheObject> cacheMap = new HashMap<String,
    // CacheObject>();

    public static void insertCache(String cacheKey, Object cacheValue) {
        try {
            // CacheObject cacheO = new CacheObject(cacheValue,cacheKey);
            // cacheMap.put(cacheKey, cacheO);
            if (cacheValue instanceof String) {
                RedisClient.addStringToJedis(cacheKey, (String) cacheValue);
            } else {
                RedisClient.addStringToJedis(cacheKey,
                                             JsonUtil.serialize(cacheValue));
            }
        } catch (Exception e) {
            logger.warn("插入缓存异常！cacheKey:" + cacheKey, e);
        }
    }

    public static void insertCache(String cacheKey, Object cacheValue,
            int dueTime) {
        try {
            // CacheObject cacheO = new CacheObject(cacheValue,cacheKey,
            // dueTime);
            // cacheMap.put(cacheKey, cacheO);
            if (cacheValue instanceof String) {
                RedisClient.addStringToJedis(cacheKey, (String) cacheValue,
                                             dueTime);
            } else {
                RedisClient.addStringToJedis(cacheKey,
                                             JsonUtil.serialize(cacheValue),
                                             dueTime);
            }
        } catch (Exception e) {
            logger.warn("插入缓存异常！cacheKey:" + cacheKey, e);
        }
    }

    public static String getCacheValue(String cacheKey) {
        try {
            return (String) getCacheJson(cacheKey, String.class);
        } catch (Exception e) {
            logger.warn("获取缓存异常！cacheKey:" + cacheKey, e);
            return null;
        }
    }

    public static Object getCacheJson(String cacheKey, Class<?> clz) {
        if (StringUtils.isBlank(cacheKey)) {
            return null;
        }
        String value = null;
        try {
            value = RedisClient.getStringFromJedis(cacheKey);
            if (value != null && !String.class.equals(clz)) {
                return JsonUtil.deserialize(value, clz);
            }
        } catch (Exception e) {
            logger.warn("获取缓存异常！cacheKey:" + cacheKey, e);
        }
        return value;
    }

    /**
     * // * 删除缓存
     * 
     * @param key
     * @param dbName
     * @param tableName
     * @throws Exception
     */
    public static void deleteFromCache(String key) throws Exception {
        // cacheMap.remove(key);
        RedisClient.delKeyFromJedis(key);
    }

}
