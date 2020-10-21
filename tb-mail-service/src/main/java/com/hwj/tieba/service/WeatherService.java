package com.hwj.tieba.service;

import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.WeatherVo;

public interface WeatherService {
    /**
     * 查询天气
     * @param ipAddress ip地址
     * @return
     */
    ServerResponse<WeatherVo> queryWeather(String ipAddress);
}
