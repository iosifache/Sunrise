package ro.mta.se.lab.models;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class WeatherConditions {

  private boolean isPopulated = false;
  private String overallWeatherPicture;
  private float temperature;
  private float windSpeed;
  private float cloudiness;
  private float precipitationVolume;

  public WeatherConditions(
      boolean isPopulated,
      String overallWeatherPicture,
      float temperature,
      float windSpeed,
      float cloudiness,
      float precipitationVolume) {
    this.isPopulated = isPopulated;
    this.overallWeatherPicture = overallWeatherPicture;
    this.temperature = temperature;
    this.windSpeed = windSpeed;
    this.cloudiness = cloudiness;
    this.precipitationVolume = precipitationVolume;
  }

  public void setOverallWeatherPicture(String overallWeatherPicture) {
    this.isPopulated = true;
    this.overallWeatherPicture = overallWeatherPicture;
  }

  public Image getOverallWeatherPicture() {
    return new Image("http://openweathermap.org/img/wn/" + this.overallWeatherPicture + "@2x.png");
  }

  public SimpleObjectProperty<Image> overallWeatherPictureProperty() {
    if (!isPopulated) return new SimpleObjectProperty<Image>(null);
    return new SimpleObjectProperty<Image>(this.getOverallWeatherPicture());
  }

  public void setTemperature(float temperature) {
    this.isPopulated = true;
    this.temperature = temperature;
  }

  public String getTemperature() {
    if (!isPopulated) return "";
    return String.format("%.1f° C", this.temperature);
  }

  public StringProperty temperatureProperty() {
    return new SimpleStringProperty(this.getTemperature());
  }

  public void setWindSpeed(float windSpeed) {
    this.isPopulated = true;
    this.windSpeed = windSpeed;
  }

  public String getWindSpeed() {
    if (!isPopulated) return "";
    return String.format("%.1f km/h", this.windSpeed);
  }

  public StringProperty windSpeedProperty() {
    return new SimpleStringProperty(this.getWindSpeed());
  }

  public void setCloudiness(float cloudiness) {
    this.isPopulated = true;
    this.cloudiness = cloudiness;
  }

  public String getCloudiness() {
    if (!isPopulated) return "";
    return String.format("%d%%", (int) this.cloudiness);
  }

  public StringProperty cloudinessProperty() {
    return new SimpleStringProperty(this.getCloudiness());
  }

  public void setPrecipitationVolume(float precipitationVolume) {
    this.isPopulated = true;
    this.precipitationVolume = precipitationVolume;
  }

  public String getPrecipitationVolume() {
    if (!isPopulated) return "";
    return String.format("%.1f mm", this.precipitationVolume);
  }

  public StringProperty precipitationVolumeProperty() {
    return new SimpleStringProperty(this.getPrecipitationVolume());
  }
}
