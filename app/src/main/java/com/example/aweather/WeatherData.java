package com.example.aweather;

import android.util.Log;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class WeatherData {

    public class HoursWeather {

        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm");

        private String time;
        private String temp;
        private String sky;
        private String iconId;

        public String getIconId() {
            return iconId;
        }

        public String getTime() {
            return time;
        }

        public String getTemp() {
            return temp;
        }

        public String getSky() {
            return sky;
        }

        public void parseJSONObject (JSONObject json){
            try{
                time = formatter.format(json.getLong("dt")*1000);
                temp = String.format("%.1f ℃", json.getJSONObject("main").getDouble("temp"));
                sky = json.getJSONArray("weather").getJSONObject(0).getString("description");
                iconId = json.getJSONArray("weather").getJSONObject(0).getString("icon");
            }
            catch (Exception ex) {
                Log.d("ParserLog", ex.getMessage());
            }
        }

    }

    public class DailyWeather {

        SimpleDateFormat formatter= new SimpleDateFormat("EEE, dd.mm");

        private String date;
        private String sky;
        private String temp;
        private String iconId;

        public String getIconId() {
            return iconId;
        }

        public String getDate() {
            return date;
        }

        public String getSky() {
            return sky;
        }

        public String getTemp() {
            return temp;
        }

        public void parseJSONObject (JSONObject json){
            try{
                date = formatter.format(json.getLong("dt")*1000);
                temp = String.format("%.1f ℃", json.getJSONObject("main").getDouble("temp"));
                sky = json.getJSONArray("weather").getJSONObject(0).getString("description");
                iconId = json.getJSONArray("weather").getJSONObject(0).getString("icon");
            }
            catch (Exception ex) {
                Log.d("ParserLog", ex.getMessage());
            }
        }
    }

    private String city;
    private String currentTemp;
    private String main;
    private String currentSky;
    private String iconId;
    public ArrayList<HoursWeather> hoursWeather;
    public ArrayList<DailyWeather> dailyWeather;

    public String getCity() {
        return city;
    }

    public String getCurentTemp() {
        return currentTemp;
    }

    public String getMain() {
        return main;
    }

    public String getCurentSky() {
        return currentSky;
    }

    public String getIconId() {
        return iconId;
    }

    public WeatherData(String city) {
        this.city = city;
        hoursWeather = new ArrayList<>();
        dailyWeather = new ArrayList<>();

    }

    public void parseJSONObjects(JSONObject current, JSONObject forecast) {
        parseCurrentWeatherJSONObject(current);
        parseForecastWeatherJSONObject(forecast);
    }

    public void parseCurrentWeatherJSONObject(JSONObject json) {
        try {
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            main = details.getString("main");
            currentSky = details.getString("description");
            iconId = details.getString("icon");
            JSONObject main = json.getJSONObject("main");
            currentTemp = String.format("%.1f ℃", main.getDouble("temp"));
        } catch (Exception ex) {
            Log.d("ParserLog", ex.getMessage());
        }
    }

    public void parseForecastWeatherJSONObject(JSONObject json) {
        try {
            for (int i = 0; i<=3; ++i) {
                JSONObject data = json.getJSONArray("list").getJSONObject(i);
                HoursWeather hour = new HoursWeather();
                hour.parseJSONObject(data);
                hoursWeather.add(hour);
            }

            for (int i = 0; i<=40; i+=9){
                JSONObject data = json.getJSONArray("list").getJSONObject(i);
                DailyWeather day = new DailyWeather();
                day.parseJSONObject(data);
                dailyWeather.add(day);
            }

            JSONObject data = json.getJSONArray("list").getJSONObject(0);
        } catch (Exception ex) {
            Log.d("ParserLog", ex.getMessage());
        }
    }


}
