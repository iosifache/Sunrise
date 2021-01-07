package ro.mta.se.lab.models;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class WeatherConditions {

  static final String ICON_URL_FORMAT_STRING = "http://openweathermap.org/img/wn/%s@2x.png";
  static final String TEMPERATURE_LABEL_SUFFIX = "° C";
  static final String WIND_SPEED_LABEL_SUFFIX = " km/h";
  static final String CLOUDINESS_LABEL_SUFFIX = "%%";
  static final String PRECIPITATION_VOLUME_LABEL_SUFFIX = " mm";

  private boolean isPopulated = false;
  private String pictureId;
  private float temperature;
  private float windSpeed;
  private float cloudiness;
  private float precipitationVolume;

  public WeatherConditions(
      boolean isPopulated,
      String pictureId,
      float temperature,
      float windSpeed,
      float cloudiness,
      float precipitationVolume) {
    this.isPopulated = isPopulated;
    this.pictureId = pictureId;
    this.temperature = temperature;
    this.windSpeed = windSpeed;
    this.cloudiness = cloudiness;
    this.precipitationVolume = precipitationVolume;
  }

  public void setPictureId(String pictureId) {
    this.isPopulated = true;
    this.pictureId = pictureId;
  }

  public Image getPictureId() {
    if (!isPopulated) return null;
    return new Image(String.format(ICON_URL_FORMAT_STRING, this.pictureId));
  }

  public SimpleObjectProperty<Image> pictureIdProperty() {
    return new SimpleObjectProperty<Image>(this.getPictureId());
  }

  public void setTemperature(float temperature) {
    this.isPopulated = true;
    this.temperature = temperature;
  }

  public String getTemperature() {
    if (!isPopulated) return "";
    return String.format("%.1f" + TEMPERATURE_LABEL_SUFFIX, this.temperature);
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
    return String.format("%.1f" + WIND_SPEED_LABEL_SUFFIX, this.windSpeed);
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
    return String.format("%d" + CLOUDINESS_LABEL_SUFFIX, (int) this.cloudiness);
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
    return String.format("%.1f" + PRECIPITATION_VOLUME_LABEL_SUFFIX, this.precipitationVolume);
  }

  public StringProperty precipitationVolumeProperty() {
    return new SimpleStringProperty(this.getPrecipitationVolume());
  }

  @Override
  public String toString() {
    return "- picture ID: "
        + this.pictureId
        + "\n"
        + "- temperature: "
        + this.temperature
        + "\n"
        + "- wind speed: "
        + this.windSpeed
        + "\n"
        + "- cloudiness: "
        + this.cloudiness
        + "\n"
        + "- precipitation volume: "
        + this.precipitationVolume;
  }
}
