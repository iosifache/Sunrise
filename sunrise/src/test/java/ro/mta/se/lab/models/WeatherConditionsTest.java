package ro.mta.se.lab.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeatherConditionsTest {

  private WeatherConditions weather;

  @BeforeEach
  void setup(){
    this.weather = new WeatherConditions();
  }

  @Test
  void testPictureIdHandling() {
    this.weather.setPictureId("10n");
    Assertions.assertEquals(
        "http://openweathermap.org/img/wn/10n@2x.png", this.weather.getPictureUrl());
  }

  @Test
  void testTemperatureHandling() {
    this.weather.setTemperature(10);
    Assertions.assertEquals("10.0° C", this.weather.getTemperature());
  }

  @Test
  void testWindSpeedHandling() {
    this.weather.setWindSpeed(15);
    Assertions.assertEquals("15.0 m/s", this.weather.getWindSpeed());
  }

  @Test
  void testCloudinessHandling() {
    this.weather.setCloudiness(100);
    Assertions.assertEquals("100%", this.weather.getCloudiness());
  }

  @Test
  void testPrecipitationVolumeHandling() {
    this.weather.setPrecipitationVolume(10);
    Assertions.assertEquals("10.0 mm", weather.getPrecipitationVolume());
  }

  @Test
  void testStringify() {

    String STRINGIFY_EXPECTED_RESULT = """
- picture ID: http://openweathermap.org/img/wn/10n@2x.png
- temperature: 10.0° C
- wind speed: 15.0 m/s
- cloudiness: 100%
- precipitation volume: 10.0 mm""";

    this.weather = new WeatherConditions(true, "10n", 10, 15, 100, 10);
    Assertions.assertEquals(STRINGIFY_EXPECTED_RESULT, this.weather.toString());

  }

}
