package com.yunjun.auto.common.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author baoxu
 * 
 * @version 1.0
 * 
 */
public class JsonUtil {

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper _om;

    private static synchronized ObjectMapper getOM() {
        if (_om != null)
            return _om;
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        _om = om;
        return om;
    }

    /**
     * @description 根据json字符串，反序列化成类型为cls的java对象
     * @author baoxu
     * @create 2015年3月12日下午4:26:44
     * @version 1.0
     * @param json
     *            json字符串
     * @param cls
     *            需要被序列化的java类型
     * @return
     */
    public static Object deserialize(String json, Class<?> cls) {
        return deserialize(json, cls, null);
    }

    /**
     * @description 根据json字符串，反序列化成类型为cls的java对象
     * @author baoxu
     * @create 2015年5月26日上午11:43:52
     * @version 1.0
     * @param json
     *            json字符串
     * @param cls
     *            需要被序列化的java类型
     * @param genericType
     *            java 泛型 具体类型
     * @return
     */
    public static Object deserialize(String json, Class<?> cls,
            Class<?>... genericType) {
        Object value = null;
        try {
            if (genericType == null || genericType.length == 0) {
                value = getOM().readValue(json, cls);
            } else {
                JavaType javaType = getOM().getTypeFactory()
                        .constructParametricType(cls, genericType);
                value = getOM().readValue(json, javaType);
            }
            return value;
        } catch (JsonGenerationException e) {
            logger.error("反序列化json字符串发生异常！字符串为：" + json, e);
        } catch (JsonMappingException e) {
            logger.error("反序列化json字符串发生异常！字符串为：" + json, e);
        } catch (IOException e) {
            logger.error("反序列化json字符串发生异常！字符串为：" + json, e);
        }
        return value;
    }

    /**
     * @description 根据java对象，序列化成json字符串
     * @author baoxu
     * @create 2015年3月12日下午4:30:33
     * @version 1.0
     * @param o
     *            需要生成json串的java对象
     * @return
     */
    public static String serialize(Object o) {
        try {
            if (o == null || o instanceof String) {
                return (String) o;
            }
            String v = getOM().writeValueAsString(o);
            return v;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            logger.error("生成json字符串发生异常！对象为：" + o, e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            logger.error("生成json字符串发生异常！对象为：" + o, e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("生成json字符串发生异常！对象为：" + o, e);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(JsonUtil.serialize(null));
        System.out.println(JsonUtil.serialize("haha"));
    }
}
