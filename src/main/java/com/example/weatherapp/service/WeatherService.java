package com.example.weatherapp.service;

import com.example.weatherapp.model.WeatherData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherService {
    private static final String API_KEY = "c7ed300c82bc5ed49d276229680abb5f";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_API_URL = "https://api.openweathermap.org/data/2.5/forecast";

    public WeatherData getCurrentWeather(String city, boolean isCelsius) throws Exception {
        String units = isCelsius ? "metric" : "imperial";
        String urlStr = WEATHER_API_URL + "?q=" + URLEncoder.encode(city, "UTF-8") +
                "&appid=" + API_KEY + "&units=" + units;

        String jsonResponse = makeApiRequest(urlStr);
        return parseWeatherData(jsonResponse);
    }

    private String makeApiRequest(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("API request failed with response code: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    private WeatherData parseWeatherData(String jsonResponse) throws Exception {
        try {
            JSONObject json = new JSONObject(jsonResponse);

            WeatherData data = new WeatherData();
            data.setCity(json.getString("name"));
            data.setCountry(json.getJSONObject("sys").getString("country"));

            JSONObject main = json.getJSONObject("main");
            data.setTemperature(main.getDouble("temp"));
            data.setFeelsLike(main.getDouble("feels_like"));
            data.setHumidity(main.getInt("humidity"));

            JSONObject wind = json.getJSONObject("wind");
            data.setWindSpeed(wind.getDouble("speed"));

            JSONArray weatherArray = json.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            data.setDescription(weather.getString("description"));
            data.setIconCode(weather.getString("icon"));

            return data;
        } catch (Exception e) {
            throw new Exception("Error parsing weather data: " + e.getMessage());
        }
    }
}