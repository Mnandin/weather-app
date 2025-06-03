package com.example.weatherapp.model;

import java.time.LocalDateTime;

public class SearchHistoryItem {
    private String city;
    private LocalDateTime timestamp;

    public SearchHistoryItem(String city, LocalDateTime timestamp) {
        this.city = city;
        this.timestamp = timestamp;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}