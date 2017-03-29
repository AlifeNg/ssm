package com.yunjun.auto.datacache.common.redis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yunjun.auto.common.utils.PropertiesUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis操作基础类
 * 
 * @author lschen
 * 
 */
public class RedisClient {

    private static final int defaultcacheSeconds = 120;

    private static Logger logger = LoggerFactory.getLogger(RedisClient.class);

    @Autowired
    private static JedisPool jedisPool;

    private static void initJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMaxIdle(100);
        config.setMaxWaitMillis(10000L);
        // 测试连接是否正常
        config.setTestOnBorrow(true);
        config.setMinIdle(0);
        String host = PropertiesUtils.readApplicationValue("redis.host");
        String port = PropertiesUtils.readApplicationValue("redis.port");
        if (StringUtils.isBlank(port)) {
            port = Protocol.DEFAULT_PORT + "";
        }
        String timeout = PropertiesUtils.readApplicationValue("redis.timeout");
        int timeoutInt = 2000;
        if (StringUtils.isNotBlank(timeout)) {
            try {
                timeoutInt = Integer.parseInt(timeout);
            } catch (NumberFormatException e) {
                logger.warn("redis timeout 参数配置非整数，将启用默认配置 2秒钟", e);
            }
        }
        jedisPool = new JedisPool(config, host, Integer.parseInt(port),
                timeoutInt);
    }

    /**
     * @description 获取jedis缓存池
     * @author baoxu
     * @create 2015年5月27日下午3:36:12
     * @version 1.0
     * @return
     */
    private static synchronized JedisPool getJedisPool() {
        if (jedisPool == null) {
            initJedisPool();
        }
        return jedisPool;
    }

    /**
     * 得到redis实例
     * 
     * @return jedis 实例
     * @throws JedisException
     */
    public static Jedis getJedis() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = getJedisPool().getResource();
        } catch (JedisException e) {
            logger.warn("failed:jedisPool getResource.", e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
        return jedis;
    }

    /**
     * 释放
     * 
     * @param jedis
     */
    public static void release(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public static String addStringToJedis(String key, String value) {
        return addStringToJedis(key, value, defaultcacheSeconds);
    }

    /**
     * 添加string类型
     * 
     * @param key
     * @param value
     * @param cacheSeconds
     * @return
     */
    public static String addStringToJedis(String key, String value,
            int cacheSeconds) {
        Jedis jedis = null;
        String lastVal = null;
        try {
            jedis = getJedis();

            lastVal = jedis.getSet(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return lastVal;
    }

    /**
     * 批量添加string类型
     * 
     * @param batchData
     * @param cacheSeconds
     */
    public static void addStringToJedis(Map<String, String> batchData,
            int cacheSeconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            Pipeline pipeline = jedis.pipelined();
            for (Map.Entry<String, String> element : batchData.entrySet()) {
                if (cacheSeconds != 0) {
                    pipeline.setex(element.getKey(), cacheSeconds,
                                   element.getValue());
                } else {
                    pipeline.set(element.getKey(), element.getValue());
                }
            }
            pipeline.sync();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 添加list类型
     * 
     * @param key
     * @param list
     * @param cacheSeconds
     */
    public static void addListToJedis(String key, List<String> list,
            int cacheSeconds) {
        if (list != null && list.size() > 0) {
            Jedis jedis = null;
            try {
                jedis = getJedis();

                if (jedis.exists(key)) {
                    jedis.del(key);
                }
                for (String aList : list) {
                    jedis.rpush(key, aList);
                }
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } catch (JedisException e) {
                logger.warn("failed:", e);
            } catch (Exception e) {
                logger.warn("failed:", e);
            } finally {
                release(jedis);
            }
        }
    }

    /**
     * 添加set类型
     * 
     * @param key
     * @param value
     */
    public static void addToSetJedis(String key, String[] value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.sadd(key, value);
        } catch (Exception e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 删除set类型
     * 
     * @param key
     * @param value
     */
    public static void removeSetJedis(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.srem(key, value);
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 往list中追加
     * 
     * @param key
     * @param data
     * @param cacheSeconds
     */
    public static void pushDataToListJedis(String key, String data,
            int cacheSeconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.rpush(key, data);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 往list中批量追加
     * 
     * @param key
     * @param batchData
     * @param cacheSeconds
     */
    public static void pushDataToListJedis(String key, List<String> batchData,
            int cacheSeconds) {
        Jedis jedis = null;

        try {
            jedis = getJedis();

            jedis.del(key);
            jedis.lpush(key, batchData.toArray(new String[batchData.size()]));
            if (cacheSeconds != 0)
                jedis.expire(key, cacheSeconds);
        } catch (Exception e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 删除list中的元素
     * 
     * @param key
     * @param values
     * @param methodName
     */
    public static void deleteDataFromListJedis(String key, List<String> values) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            Pipeline pipeline = jedis.pipelined();
            if (values != null && !values.isEmpty()) {
                for (String val : values) {
                    pipeline.lrem(key, 0, val);
                }
            }
            pipeline.sync();
        } catch (Exception e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 添加hash类型
     * 
     * @param key
     * @param map
     * @param cacheSeconds
     */
    public static void addHashMapToJedis(String key, Map<String, String> map,
            int cacheSeconds) {
        Jedis jedis = null;
        if (map != null && map.size() > 0) {
            try {
                jedis = getJedis();

                jedis.hmset(key, map);
                if (cacheSeconds >= 0)
                    jedis.expire(key, cacheSeconds);
            } catch (Exception e) {

                logger.warn("failed:", e);
            } finally {
                release(jedis);
            }
        }
    }

    /**
     * 添加hash类型
     * 
     * @param key
     * @param field
     * @param value
     * @param cacheSeconds
     */
    public static void addHashMapToJedis(String key, String field,
            String value, int cacheSeconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null) {

                jedis.hset(key, field, value);
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 修改hash类型
     * 
     * @param key
     * @param incrementField
     * @param incrementValue
     * @param dateField
     * @param dateValue
     */
    public static void updateHashMapToJedis(String key, String incrementField,
            long incrementValue, String dateField, String dateValue) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.hincrBy(key, incrementField, incrementValue);
            jedis.hset(key, dateField, dateValue);
        } catch (Exception e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 获取string类型value
     * 
     * @param key
     * @return
     */
    public static String getStringFromJedis(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();

            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = StringUtils.isNotBlank(value)
                        && !"nil".equalsIgnoreCase(value) ? value : null;
            }
        } catch (Exception e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return value;
    }

    /**
     * 批量获取String类型value
     * 
     * @param keys
     * @return
     */
    public static List<String> getStringFromJedis(String[] keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            return jedis.mget(keys);
        } catch (Exception e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 获取list类型数据
     * 
     * @param key
     * @return
     */
    public static List<String> getListFromJedis(String key) {
        List<String> list = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();

            if (jedis.exists(key)) {
                list = jedis.lrange(key, 0, -1);
            }
        } catch (JedisException e) {

            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return list;
    }

    /**
     * 获取set类型数据
     * 
     * @param key
     * @return
     */
    public static Set<String> getSetFromJedis(String key) {
        Set<String> list = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();

            if (jedis.exists(key)) {
                list = jedis.smembers(key);
            }
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return list;
    }

    /**
     * 获取hash类型全部键值对
     * 
     * @param key
     * @return
     */
    public static Map<String, String> getHashMapFromJedis(String key) {
        Map<String, String> hashMap = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            hashMap = jedis.hgetAll(key);
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return hashMap;
    }

    /**
     * 获取hash类型中指定field的值
     * 
     * @param key
     * @param field
     * @return
     */
    public static String getHashMapValueFromJedis(String key, String field) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null) {
                if (jedis.exists(key)) {
                    value = jedis.hget(key, field);
                }
            }
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return value;
    }

    /**
     * 
     * @param identifyName
     * @param methodName
     * @return
     */
    public static Long getIdentifyId(String identifyName, String methodName) {
        Jedis jedis = null;
        Long identify = null;
        try {
            jedis = getJedis();
            if (jedis != null) {
                identify = jedis.incr(identifyName);
                if (identify == 0) {
                    return jedis.incr(identifyName);
                } else {
                    return identify;
                }
            }
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 删除某db的某个key值
     * 
     * @param key
     * @return
     */
    public static Long delKeyFromJedis(String key) {
        Jedis jedis = null;
        long result = 0;
        try {
            jedis = getJedis();
            return jedis.del(key);
        } catch (Exception e) {
            logger.warn("failed", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    /**
     * 根据dbIndex flushDB每个shard
     * 
     * @param dbIndex
     */
    public static void flushDBFromJedis() {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.flushDB();
        } catch (Exception e) {
            logger.warn("failed", e);
        } finally {
            release(jedis);
        }
    }

    /**
     * 判断某个key是否存在
     * 
     * @param key
     * @return
     */
    public static boolean existKey(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.exists(key);
        } catch (Exception e) {
            logger.warn("failed:", e);
        } finally {
            release(jedis);
        }
        return false;
    }

}