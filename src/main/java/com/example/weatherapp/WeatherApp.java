package com.example.weatherapp;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WeatherApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WeatherApp.class.getResource("weather-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 650);
        scene.getStylesheets().add(getClass().getResource("/com/example/weatherapp/styles/styles.css").toExternalForm());
        stage.setTitle("Weather App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}