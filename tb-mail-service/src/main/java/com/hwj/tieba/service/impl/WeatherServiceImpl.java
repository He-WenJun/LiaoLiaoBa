package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.WeatherService;
import com.hwj.tieba.util.ApiCallUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.WeatherVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Autowired
    private ApiCallUtil apiCallUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<WeatherVo> queryWeather(String ipAddress) {
        //查询ip对应的城市
        ipAddress = "123.125.71.38";
        String locationJsonStr = apiCallUtil.queryIP(ApiCallUtil.KEY, ipAddress, ApiCallUtil.OUTPUT_TYPE.JSON);
        JSONObject locationJsonObject = JSON.parseObject(locationJsonStr);
        if(locationJsonObject.getInteger("status") == 0){
            throw new TieBaException("查询城市时，status为0，无法查询城市");
        }
        //取出城市
        String city = locationJsonObject.getString("city");

        String key = "TB:WEATHER:" + city;

        if(redisUtil.hasKey(key)){
           WeatherVo weatherVo = redisUtil.get(key, WeatherVo.class);
           return ServerResponse.createBySuccess(weatherVo);
        }

        //查询实时天气
        String baseWeatherJsonStr = apiCallUtil.queryWeather(ApiCallUtil.KEY, city, ApiCallUtil.EXTENSIONS_TYPE.BASE);
        //查询未来天气
        String allWeatherJsonStr = apiCallUtil.queryWeather(ApiCallUtil.KEY, city, ApiCallUtil.EXTENSIONS_TYPE.ALL);

        JSONObject baseWeatherJsonObj = JSON.parseObject(baseWeatherJsonStr);
        JSONObject allWeatherJsonObj = JSON.parseObject(allWeatherJsonStr);

        if(baseWeatherJsonObj.getInteger("status") == 0 || allWeatherJsonObj.getInteger("status") == 0){
            throw new TieBaException("查询天气时，status为0，查询失败");
        }

         WeatherVo weatherVo = JSON.parseObject(baseWeatherJsonObj.getJSONArray("lives").get(0).toString(),WeatherVo.class);
         weatherVo.setForecastWeathers(JSON.parseArray(allWeatherJsonObj.getJSONArray("forecasts").getJSONObject(0).getString("casts"),WeatherVo.ForecastWeather.class));

         redisUtil.set(key, 60*60, weatherVo);

        return ServerResponse.createBySuccess(weatherVo);
    }
}
