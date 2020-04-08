package com.hwj.tieba.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    RedisTemplate<String,String> redisTemplate;


    public <T> T get(String key,Class<T> type){
        String jsonData = redisTemplate.opsForValue().get(key);
        return JSON.parseObject(jsonData,type);
    }

    public <T> List<T> getArray(String key,Class<T> type){
        String jsonData = redisTemplate.opsForValue().get(key);
        return JSON.parseArray(jsonData,type);
    }

    public Object hget(String key){
        Map<Object,Object> jsonData = redisTemplate.opsForHash().entries(key);
        return jsonData;
    }

    public void hmset(String key,Map<String,String> map){
        redisTemplate.opsForHash().putAll(key,map);
    }

    public void set(String key,Integer time,Object objData){
        String jsonData = JSON.toJSONString(objData);
        redisTemplate.opsForValue().set(key,jsonData,time, TimeUnit.SECONDS);
    }

    public void del(String key){
        redisTemplate.delete(key);
    }
    public void hdel(String map,Object... keys){
        redisTemplate.opsForHash().delete(map,keys);
    }
    public boolean hasKey (String key){
        return redisTemplate.hasKey(key);
    }
}
