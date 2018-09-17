package com.example.mongo.utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Json 工具类
 */
public class JsonUtils {

    public static String objectToJson(final Object obj) {
        return objectToJson(obj, "yyyy-MM-dd HH:mm:ss.SSS");
    }
    
    public static String objectToJson(final Object obj, final String datePattern) {
        return JSON.toJSONStringWithDateFormat(obj, datePattern,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.DisableCircularReferenceDetect);
    }
    
    public static <T> T jsonToObject(String json, Class<T> objectClass){
    	return JSON.parseObject(json, objectClass);
    }
    
    public static <T> List<T> jsonToList(String json, Class<T> objectClass){
    	return JSON.parseArray(json, objectClass);
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String json){
    	return JSON.parseObject(json, HashMap.class);
    }
    
}
