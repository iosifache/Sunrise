package ro.mta.se.lab.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ro.mta.se.lab.models.WeatherConditions;

class WindowControllerTest {

  private WindowController controller;

  @BeforeEach
  void setup() {
    this.controller = new WindowController();
  }

  @Test
  void testMockedChangeWeather() {

    // Create a mock object for weather data
    WeatherConditions weather = Mockito.mock(WeatherConditions.class);

    // Set the getters to return constant values
    Mockito.when(weather.getPicture()).thenReturn(null);
    Mockito.when(weather.getTemperature()).thenReturn("10.0° C");
    Mockito.when(weather.getWindSpeed()).thenReturn("15.0 m/s");
    Mockito.when(weather.getCloudiness()).thenReturn("100%");
    Mockito.when(weather.getPrecipitationVolume()).thenReturn("10.0 mm");

    // Interact with the tested class
    this.controller.initializeApiKey();
    this.controller.setExternalWeather(weather);
    this.controller.changeWeather(683506);

    // Check the number of calls of the setters (the getters are not called because of an exception
    // thrown by FXML interactions)
    Mockito.verify(weather, Mockito.times(1)).setPictureId(Mockito.any());
    Mockito.verify(weather, Mockito.times(1)).setTemperature(Mockito.anyFloat());
    Mockito.verify(weather, Mockito.times(1)).setWindSpeed(Mockito.anyFloat());
    Mockito.verify(weather, Mockito.times(1)).setCloudiness(Mockito.anyFloat());
    Mockito.verify(weather, Mockito.times(1)).setPrecipitationVolume(Mockito.anyFloat());
  }
}
