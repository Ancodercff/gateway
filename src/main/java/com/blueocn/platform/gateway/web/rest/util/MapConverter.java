package com.blueocn.platform.gateway.web.rest.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Map;

/**
 * Title: MapConverter
 * Create Date: 2016-05-31 15:36
 * Description:
 *
 * @author Yufan
 * @version 1.0.0
 * @since 1.0.0
 */
public class MapConverter {

    private MapConverter() {
        // No Construct
    }

    /**
     * 将对象转换为 JSON, 然后转成 Map
     *
     * @param object 想转成 Map 的对象
     */
    public static Map<String, Object> convert(Object object) {
        String jsonStr = JSON.toJSONString(object);
        return JSON.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 将 Map 转为指定对象
     */
    public static <T> T convert(Map<String, Object> map, Class<T> clazz) {
        String jsonStr = JSON.toJSONString(map);
        return JSON.parseObject(jsonStr, clazz);
    }
}
