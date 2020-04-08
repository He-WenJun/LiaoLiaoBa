package com.hwj.tieba.util;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 请求高德地图的api接口，对用户的登录IP进行查询
 */
@FeignClient(name = "restapi.amap.com",url = "https://restapi.amap.com")
public interface QueryIPUtil {
    /**数据返回格式*/
    interface OUTPUT_TYPE{
        String JSON = "JSON";
        String XML = "XML";
    }

    /**控制台应用名*/
     String KEY = "eee0df052467b43687d0f0dc32d8b367";

    @RequestMapping(value = "/v3/ip")
    String qureyIP(@RequestParam("key") String key, @RequestParam("ip") String ip, @RequestParam("output") String outputType);

}
