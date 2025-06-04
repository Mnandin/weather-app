package com.example.weatherapp.model;

public class ForecastData {
    private String city;
    private String country;
    private DailyForecast[] dailyForecasts;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public DailyForecast[] getDailyForecasts() {
        return dailyForecasts;
    }

    public void setDailyForecasts(DailyForecast[] dailyForecasts) {
        this.dailyForecasts = dailyForecasts;
    }
}
