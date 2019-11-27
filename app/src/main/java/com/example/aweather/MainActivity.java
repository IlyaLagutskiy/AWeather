package com.example.aweather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton refreshButton;

    private String city;
    private String apiKey;
    private WeatherData data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);
        city = getString(R.string.city);
        apiKey = getString(R.string.apiKey);
        data = new WeatherData(city);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new currentWeatherUpdate().execute();
        new forecastWeatherUpdate().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refreshButton:
                new currentWeatherUpdate().execute();
                new forecastWeatherUpdate().execute();
                break;
        }
    }

    class currentWeatherUpdate extends AsyncTask<Void, String, JSONObject> {

        private static final String OPEN_WEATHER_MAP_API =
                "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            data.parseCurrentWeatherJSONObject(jsonObject);
            updateGuiWithCurrentWeather(data);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city, apiKey));
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONObject data = new JSONObject(json.toString());
                Log.d("JSON", json.toString());

                // This value will be 404 if the request was not
                // successful
                if (data.getInt("cod") != 200) {
                    return null;
                }
                return data;
            } catch (Exception e) {
                return null;
            }
        }
    }

    class forecastWeatherUpdate extends AsyncTask<Void, String, JSONObject> {

        private static final String OPEN_WEATHER_MAP_API =
                "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&appid=%s";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            data.parseForecastWeatherJSONObject(jsonObject);
            updateGuiWithHourlyWeather(data);
            updateGuiWithDailyWeather(data);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city, apiKey));
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp = "";
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONObject data = new JSONObject(json.toString());
                Log.d("JSON", json.toString());

                // This value will be 404 if the request was not
                // successful
                if (data.getInt("cod") != 200) {
                    return null;
                }
                return data;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private void updateGuiWithCurrentWeather(WeatherData data) {
        ((TextView) findViewById(R.id.currentTemp)).setText(data.getCurentTemp());
        ((TextView) findViewById(R.id.currentCloud)).setText(data.getCurentSky());
        ((TextView) findViewById(R.id.city)).setText(data.getCity());
        ((TextView) findViewById(R.id.temp0H)).setText(data.getCurentTemp());
        changeCloudAmountImage(R.id.cloud0H, data.getIconId());
    }

    private void updateGuiWithHourlyWeather(WeatherData data) {
        for (int i = 1; i < 5; i++) {
            int timeId = getResources().getIdentifier("time" + i * 3 + "H", "id", "com.example.aweather");
            int cloudId = getResources().getIdentifier("cloud" + i * 3 + "H", "id", "com.example.aweather");
            int tempId = getResources().getIdentifier("temp" + i * 3 + "H", "id", "com.example.aweather");
            ((TextView) findViewById(timeId)).setText(data.hoursWeather.get(i-1).getTime());
            ((TextView) findViewById(tempId)).setText(data.hoursWeather.get(i-1).getTemp());
            changeCloudAmountImage(cloudId, data.hoursWeather.get(i-1).getIconId());
        }
    }

    private void updateGuiWithDailyWeather(WeatherData data) {
        for (int i = 0; i < 5; i++) {
            int dateId = getResources().getIdentifier("date" + i + "D", "id", "com.example.aweather");
            int cloudId = getResources().getIdentifier("cloud" + i + "D", "id", "com.example.aweather");
            int tempId = getResources().getIdentifier("temp" + i + "D", "id", "com.example.aweather");
            ((TextView) findViewById(dateId)).setText(data.dailyWeather.get(i).getDate());
            ((TextView) findViewById(tempId)).setText(data.dailyWeather.get(i).getTemp());
            changeCloudAmountImage(cloudId, data.dailyWeather.get(i).getIconId());
        }
    }

    private void changeCloudAmountImage(int viewId, String iconId) {
        ImageView v = findViewById(viewId);
        switch (iconId) {
            case "01d":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weathersunnyicon));
                break;
            case "01n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weatherclearnighticon));
                break;
            case "02d":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weatherpartlysunnyicon));
                break;
            case "02n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weatherpartlycloudynighticon));
                break;
            case "03d":
            case "03n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weathercloudyicon));
                break;
            case "04d":
            case "04n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weathersmokeicon));
                break;
            case "09d":
            case "09n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weatherheavyrainicon));
                break;
            case "10d":
            case "10n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weatherrainicon));
                break;
            case "11d":
            case "11n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weatherthunderstormicon));
                break;
            case "13d":
            case "13n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weathersnowicon));
                break;
            case "50d":
            case "50n":
                v.setImageDrawable(getResources().getDrawable(R.drawable.ios11weatherfogicon));
                break;
        }
    }


}

