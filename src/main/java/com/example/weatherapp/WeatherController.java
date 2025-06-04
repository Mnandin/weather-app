package com.example.weatherapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import com.example.weatherapp.model.*;
import com.example.weatherapp.service.WeatherService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
                addToHistory(city);

                try {
                    ForecastData forecastData = weatherService.getForecast(city, isCelsius);
                    Platform.runLater(() -> updateForecastUI(forecastData));
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Error fetching forecast: " + e.getMessage()));
                }

            } catch (Exception e) {
                Platform.runLater(() -> showError(e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void toggleUnit() {
        isCelsius = !isCelsius;
        unitToggle.setText(isCelsius ? "°C" : "°F");

        if (!currentCity.isEmpty()) {
            searchWeather();
        }
    }

    private void updateForecastUI(ForecastData forecastData) {
        forecastBox.getChildren().clear();

        Label headerLabel = new Label("5-Day Forecast");
        headerLabel.getStyleClass().add("forecast-header");
        forecastBox.getChildren().add(headerLabel);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        int col = 0;
        for (DailyForecast forecast : forecastData.getDailyForecasts()) {
            VBox forecastCard = createForecastCard(forecast);
            gridPane.add(forecastCard, col++, 0);
        }

        forecastBox.getChildren().add(gridPane);
    }

    private VBox createForecastCard(DailyForecast forecast) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("forecast-card");

        Label dateLabel = new Label(forecast.getDate());
        dateLabel.setTextFill(Color.WHITE);

        ImageView iconView = new ImageView();
        try {
            String iconUrl = "https://openweathermap.org/img/wn/" + forecast.getIconCode() + ".png";
            iconView.setImage(new Image(iconUrl));
            iconView.setFitHeight(50);
            iconView.setFitWidth(50);
        } catch (Exception e) {
            System.out.println("API request failed with response code: " + e);
        }

        Label tempLabel = new Label(String.format("%.1f°%s", forecast.getTemperature(), isCelsius ? "C" : "F"));
        tempLabel.setTextFill(Color.WHITE);
        Label descLabel = new Label(capitalizeFirstLetter(forecast.getDescription()));
        descLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(dateLabel, iconView, tempLabel, descLabel);
        return card;
    }

    private void updateWeatherUI(WeatherData data) {
        cityLabel.setText(data.getCity() + ", " + data.getCountry());

        try {
            String iconUrl = "https://openweathermap.org/img/wn/" + data.getIconCode() + "@2x.png";
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
        statusLabel.setTextFill(Color.WHITE);
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

    private void addToHistory(String city) {
        LocalDateTime now = LocalDateTime.now();
        SearchHistoryItem item = new SearchHistoryItem(city, now);

        Platform.runLater(() -> {
            searchHistory.removeIf(historyItem -> historyItem.getCity().equalsIgnoreCase(city));
            searchHistory.add(0, item);

            if (searchHistory.size() > 10) {
                searchHistory.remove(searchHistory.size() - 1);
            }
        });
    }

    private class SearchHistoryCell extends ListCell<SearchHistoryItem> {
        @Override
        protected void updateItem(SearchHistoryItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);

                Label cityLabel = new Label(item.getCity());
                cityLabel.setFont(Font.font(14));

                Label timeLabel = new Label(item.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                Button searchButton = new Button("Search Again");
                searchButton.setOnAction(e -> {
                    cityField.setText(item.getCity());
                    searchWeather();
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.SOMETIMES);

                hbox.getChildren().addAll(cityLabel, spacer, timeLabel, searchButton);
                setGraphic(hbox);
            }
        }
    }
}
