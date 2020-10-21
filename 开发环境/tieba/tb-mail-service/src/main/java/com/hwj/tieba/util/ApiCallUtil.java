package com.hwj.tieba.util;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 请求高德地图api接口的工具类
 */
@FeignClient(name = "restapi.amap.com",url = "https://restapi.amap.com")
public interface ApiCallUtil {
    /**控制台应用名*/
    String KEY = "eee0df052467b43687d0f0dc32d8b367";

    /**数据返回格式*/
    interface OUTPUT_TYPE{
        String JSON = "JSON";
        String XML = "XML";
    }

    //IP地址查询，获得IP地址所在的城市
     @RequestMapping(value = "/v3/ip")
    String queryIP(@RequestParam("key") String key, @RequestParam("ip") String ip, @RequestParam("output") String outputType);


     /**返回的天气类型*/
     interface EXTENSIONS_TYPE{
         /**实时天气*/
         String BASE = "base";
         /**预测天气*/
         String ALL = "all";
     }
     //查询天气
     @RequestMapping(value = "/v3/weather/weatherInfo")
    String queryWeather(@RequestParam("key") String key,@RequestParam("city") String city, @RequestParam("extensions") String extensions);

}
