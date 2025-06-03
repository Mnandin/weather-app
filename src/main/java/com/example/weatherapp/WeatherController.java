package com.example.weatherapp;

import java.util.Calendar;

import com.example.weatherapp.model.*;
import com.example.weatherapp.service.WeatherService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class WeatherController {

    @FXML private BorderPane mainPane;
    @FXML private TextField cityField;
    @FXML private ToggleButton unitToggle;
    @FXML private Label statusLabel;
    @FXML private Label cityLabel;
    @FXML private ImageView weatherIcon;
    @FXML private Label weatherDescription;
    @FXML private Label temperatureLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;
    @FXML private VBox forecastBox;
    @FXML private ListView<SearchHistoryItem> historyListView;

    private final WeatherService weatherService = new WeatherService();
    private final ObservableList<SearchHistoryItem> searchHistory = FXCollections.observableArrayList();
    private boolean isCelsius = true;
    private String currentCity = "";

    @FXML
    public void initialize() {
        historyListView.setItems(searchHistory);
        historyListView.setCellFactory(lv -> new SearchHistoryCell());

        setDynamicBackground();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60 * 60 * 1000);
                    Platform.runLater(this::setDynamicBackground);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    @FXML
    private void searchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            showError("Please enter a city name");
            return;
        }

        currentCity = city;
        statusLabel.setText("Fetching weather data for " + city + "...");

        new Thread(() -> {
            try {
                WeatherData weatherData = weatherService.getCurrentWeather(city, isCelsius);
                Platform.runLater(() -> updateWeatherUI(weatherData));
//                addToHistory(city);
//
//                try {
//                    ForecastData forecastData = weatherService.getForecast(city, isCelsius);
//                    Platform.runLater(() -> updateForecastUI(forecastData));
//                } catch (Exception e) {
//                    Platform.runLater(() -> showError("Error fetching forecast: " + e.getMessage()));
//                }

            } catch (Exception e) {
                Platform.runLater(() -> showError("Error: " + e.getMessage()));
            }
        }).start();
    }

    public void toggleUnit(ActionEvent actionEvent) {
    }

    private void updateWeatherUI(WeatherData data) {
        cityLabel.setText(data.getCity() + ", " + data.getCountry());

        try {
            String iconUrl = "https://openweathermap.org/img/wn/" + data.getIconCode() + "@2x.png";
            System.out.println("weatherIcon: " + weatherIcon);
            weatherIcon.setImage(new Image(iconUrl));
        } catch (Exception e) {
            weatherIcon.setImage(null);
        }

        weatherDescription.setText(capitalizeFirstLetter(data.getDescription()));
        temperatureLabel.setText(String.format("%.1f°%s", data.getTemperature(), isCelsius ? "C" : "F"));
        feelsLikeLabel.setText(String.format("%.1f°%s", data.getFeelsLike(), isCelsius ? "C" : "F"));
        humidityLabel.setText(data.getHumidity() + "%");
        windLabel.setText(String.format("%.1f %s", data.getWindSpeed(), isCelsius ? "m/s" : "mph"));

        statusLabel.setText("Weather data updated successfully");
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private void setDynamicBackground() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String morningImageUrl = getClass().getResource("/com/example/weatherapp/images/morning.jpg").toExternalForm();
        String eveningImageUrl = getClass().getResource("/com/example/weatherapp/images/evening.jpg").toExternalForm();

        String backgroundStyle;
        if (hour >= 5 && hour < 19) {
            backgroundStyle = "-fx-background-image: url('" + morningImageUrl + "'); " +
                    "-fx-background-size: cover;";
        } else {
            backgroundStyle = "-fx-background-image: url('" + eveningImageUrl + "'); " +
                    "-fx-background-size: cover;";
        }

        mainPane.setStyle(backgroundStyle);
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setTextFill(Color.RED);
    }

    private class SearchHistoryCell extends ListCell<SearchHistoryItem> {
//        @Override
//        protected void updateItem(SearchHistoryItem item, boolean empty) {
//            super.updateItem(item, empty);
//
//            if (empty || item == null) {
//                setText(null);
//                setGraphic(null);
//            } else {
//                HBox hbox = new HBox(10);
//                hbox.setAlignment(Pos.CENTER_LEFT);
//
//                Label cityLabel = new Label(item.getCity());
//                cityLabel.setFont(Font.font(14));
//
//                Label timeLabel = new Label(item.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//                Button searchButton = new Button("Search Again");
//                searchButton.setOnAction(e -> {
//                    cityField.setText(item.getCity());
//                    searchWeather();
//                });
//
//                Region spacer = new Region();
//                HBox.setHgrow(spacer, Priority.ALWAYS);
//
//                hbox.getChildren().addAll(cityLabel, spacer, timeLabel, searchButton);
//                setGraphic(hbox);
//            }
//        }
    }
}
