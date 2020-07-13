package com.hwj.tieba.entity;

import java.util.Date;
import java.util.List;

public class Weather {
    /**城市名称*/
    private String city;
    /**城市所在省份*/
    private String province;
    /**实时天气描述*/
    private String realTimeWeather;
    /**实时气温*/
    private Integer realTimeTemperature;
    /**实时风向描述*/
    private String realTimeWinddirection;
    /**实时风力级别*/
    private String realTimeWindpower;
    /**空气湿度*/
    private String humidity;
    /**最后更新时间*/
    private Date reportTime;
    /**预测的天气列表*/
    List<ForecastWeather> forecastWeathers;

    public Weather(String realTimeWeatherJson){

    }

    //预测天气
    private class ForecastWeather{
        /**日期*/
        private Date date;
        /**星期*/
        private String week;
        /**白天天气*/
        private String dayWeather;
        /**晚上天气*/
        private String nightWeather;
        /**白天气温*/
        private Integer dayTemp;
        /**晚上气温*/
        private Integer nightTemp;
        /**白天风向*/
        private String dayWind;
        /**晚上风向*/
        private String nightWind;
        /**白天风力*/
        private String dayPower;
        /**晚上风力*/
        private String nightPower;
    }
}
