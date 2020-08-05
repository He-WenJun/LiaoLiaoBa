package com.hwj.tieba.vo;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.util.DateUtil;

import java.util.Date;
import java.util.List;

public class WeatherVo {
    /**城市名称*/
    private String city;
    /**城市所在省份*/
    private String province;
    /**实时天气描述*/
    private String weather;
    /**实时气温*/
    private Integer temperature;
    /**实时风向描述*/
    private String winddirection;
    /**实时风力级别*/
    private String windpower;
    /**空气湿度*/
    private Integer humidity;
    /**最后更新时间*/
    private Date reporttime;
    /**预测的天气列表*/
    List<ForecastWeather> forecastWeathers;

    //预测天气
    public static class ForecastWeather{
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

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getDayWeather() {
            return dayWeather;
        }

        public void setDayWeather(String dayWeather) {
            this.dayWeather = dayWeather;
        }

        public String getNightWeather() {
            return nightWeather;
        }

        public void setNightWeather(String nightWeather) {
            this.nightWeather = nightWeather;
        }

        public Integer getDayTemp() {
            return dayTemp;
        }

        public void setDayTemp(Integer dayTemp) {
            this.dayTemp = dayTemp;
        }

        public Integer getNightTemp() {
            return nightTemp;
        }

        public void setNightTemp(Integer nightTemp) {
            this.nightTemp = nightTemp;
        }

        public String getDayWind() {
            return dayWind;
        }

        public void setDayWind(String dayWind) {
            this.dayWind = dayWind;
        }

        public String getNightWind() {
            return nightWind;
        }

        public void setNightWind(String nightWind) {
            this.nightWind = nightWind;
        }

        public String getDayPower() {
            return dayPower;
        }

        public void setDayPower(String dayPower) {
            this.dayPower = dayPower;
        }

        public String getNightPower() {
            return nightPower;
        }

        public void setNightPower(String nightPower) {
            this.nightPower = nightPower;
        }

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public String getWinddirection() {
        return winddirection;
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public String getWindpower() {
        return windpower;
    }

    public void setWindpower(String windpower) {
        this.windpower = windpower;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Date getReporttime() {
        return reporttime;
    }

    public void setReporttime(Date reporttime) {
        this.reporttime = reporttime;
    }

    public List<ForecastWeather> getForecastWeathers() {
        return forecastWeathers;
    }

    public void setForecastWeathers(List<ForecastWeather> forecastWeathers) {
        this.forecastWeathers = forecastWeathers;
    }

}
