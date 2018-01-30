package com.lvtanxi.shared;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;


public class FastJsonParser implements Parser{
    @Override
    public <T> T fromJson(String content, Type type) throws Exception {
        return JSON.parseObject(content, type);
    }

    @Override
    public String toJson(Object body) {
        if (body==null)
            return "";
        return JSON.toJSONString(body);
    }
}
