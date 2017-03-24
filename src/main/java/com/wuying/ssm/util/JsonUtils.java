package com.wuying.ssm.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.fastjson.serializer.ValueFilter;

import net.sf.json.JSONArray;

/**
 * 
 * @ClassName JsonUtils 
 * @Description json工具类，提供json序列化方法
 * @author huxiang
 * @date 2014年9月25日 下午3:19:46 
 *
 */
public class JsonUtils {
	/**
	 * java对象序列化为JSON字符串,默认关闭循环引用检测
	 * @param obj 要序列化的对象
	 * @return json字符串
	 */
	public static String toJsonString(Object obj){
		return JSON.toJSONString(obj,SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 默认时间格式化为yyyy-MM-dd hh:mm:ss
	 * @param obj 要序列化的对象
	 * @return json字符串
	 */
	public static String toJsonStringFormatDate(Object obj){
		return JSON.toJSONString(obj,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 使用自定义格式化时间
	 * @param obj 要序列化的对象
	 * @param pattern 时间格式
	 * @return json字符串
	 */
	public static String toJsonStringFormatDate(Object obj,String pattern){
		SerializeConfig config = new SerializeConfig();
		config.put(Date.class,new SimpleDateFormatSerializer(pattern));
		return JSON.toJSONString(obj,config,SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 过滤不需要序列化的属性
	 * @param obj 要序列化的对象
	 * @param entity 需要过滤的实体类class对象
	 * @param property 需序列化的属性
	 * @return json字符串
	 */
	public static String toJsonStringFilterProperty(Object obj,Class entity,String... property){
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter(entity,property);
		return JSON.toJSONString(obj,filter,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 自定义序列化，在每个字段前面添加实体名
	 * @param obj 序列化对象
	 * @param entityName 前缀实体名称
	 * @return json字符串
	 */
	public static String toJsonStringWithName(Object obj,final String entityName){
		return toJsonStringWithName(obj,entityName,"rows","total");
	}
	/**
	 * 自定义序列化，在每个字段前面添加实体名,过滤不添加实体名字段
	 * @param obj 序列化对象
	 * @param entityName 前缀实体名称
	 * @param filterProp 过滤字段
	 * @return
	 */
	public static String toJsonStringWithName(Object obj,final String entityName,final String... filterProp){
		NameFilter filter = new NameFilter() {
			public String process(Object object, String propertyName,
					Object propertyValue) {
				for (String str : filterProp) {
					if(propertyName.equals(str)){
						return propertyName;
					}
				}
				return entityName+"."+propertyName;
			}
		};
		return JSON.toJSONString(obj,filter,SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 自定义需要转换成date类型的字段
	 * @param obj 序列化对象
	 * @param pattern 默认时间格式化
	 * @param filterProp 需要从long类型转换成date类型的字段以及需要格式化的格式
	 * @return json字符串
	 */
	public static String toJsonStringWithDate(Object obj,final String pattern,final String[]... filterProp){
		ValueFilter filter = new ValueFilter() {
			private SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			public Object process(Object object, String propertyName,
					Object propertyValue) {
				for (String[] str : filterProp) {
					if(propertyName.equals(str[0])){
						if(propertyValue instanceof Long){
							long time = (Long)propertyValue;
							if(str.length==2 && !"".equals(str[1])){
								if(!sdf.toPattern().equals(str[1])){
									sdf.applyPattern(str[1]);
								}
							}else{
								if(!sdf.toPattern().equals(pattern)){
									sdf.applyPattern(pattern);
								}
							}
							return sdf.format(new Date(time));
						}
						return propertyValue;
					}
				}
				return propertyValue;
			}
		};
		return JSON.toJSONString(obj,filter,SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 过滤集合对象里面的属性，以及对象中对象的属性
	 * @param listObject
	 * @param clazz
	 * @param property
	 * @param classMap
	 * @param filterMap
	 * @return
	 */
	public static String listToJsonStringFilterProperty(List listObject, Class clazz, String[] property, Map<String, Class> classMap, Map<String, String[]> filterMap){
		
		JSONArray jsonArray = JSONArray.fromObject(listObject);
		
		List<String> propertyList = Arrays.asList(property);
		Field[] fs = clazz.getDeclaredFields();
		List<String> lists = new ArrayList<String>();
		for (Field field : fs) {
			lists.add(field.getName());
		}
		lists.removeAll(propertyList);
		for (int i = 0; i < jsonArray.size(); i++) {
			for (String string : lists) {
				jsonArray.getJSONObject(i).remove(string);
			}
			
			for (String key : classMap.keySet()) {
				List<String> lists1 = new ArrayList<String>();
				for (Field field : classMap.get(key).getDeclaredFields()) {
					lists1.add(field.getName());
				}
				lists1.removeAll(Arrays.asList(filterMap.get(key)));
				for (String string : lists1) {
					jsonArray.getJSONObject(i).getJSONObject(key).remove(string);
				}
			}
		}
		return jsonArray.toString();
	}
}
