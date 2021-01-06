package ro.mta.se.lab.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ro.mta.se.lab.models.City;
import ro.mta.se.lab.models.Country;
import ro.mta.se.lab.models.ILocation;
import ro.mta.se.lab.models.WeatherConditions;

public class WindowController {

  private class RootElement implements ILocation {

    public String getName() {
      return "Available Countries";
    }
    ;

    public String toString() {
      return "Available Countries";
    }
    ;

    public Long getCityId() {
      return (long) -1;
    }
    ;
  }

  private static WindowController instance;
  private double x;
  private double y;
  private List<Country> countryList = new ArrayList<Country>();
  private WeatherConditions weather = new WeatherConditions(false, "", 0, 0, 0, 0);

  @FXML private ImageView weatherPicture;

  @FXML private TreeView<ILocation> locationTree;

  @FXML private Label temperatureLabel;

  @FXML private Label windSpeedLabel;

  @FXML private Label cloudinessLabel;

  @FXML private Label precipitationVolumeLabel;

  public WindowController() {
    WindowController.instance = this;
  }

  public static WindowController getInstance() {
    return WindowController.instance;
  }

  @FXML
  public void initialize() {
    try {
      // Initialize list of locations
      initializeLocationList();
      initializeTreeView();

    } catch (Exception e) {
      e.printStackTrace();
    }
    ;
  }

  @FXML
  public void clickWindow(javafx.scene.input.MouseEvent event) {
    this.x = event.getSceneX();
    this.y = event.getSceneY();
  }

  @FXML
  public void dragWindow(javafx.scene.input.MouseEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setX(event.getScreenX() - this.x);
    stage.setY(event.getScreenY() - this.y);
  }

  @FXML
  public void minimizeWindow(MouseEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setIconified(true);
  }

  @FXML
  public void closeWindow(MouseEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
  }

  public void initializeLocationList() throws IOException, ParseException {
    // Parse JSON file with locations
    JSONParser jsonParser = new JSONParser();
    String jsonFilename = this.getClass().getResource("/locations.json").getPath();
    JSONObject rawLocations = (JSONObject) jsonParser.parse(new FileReader(jsonFilename));

    // Iterate through counties
    JSONArray countries = (JSONArray) rawLocations.get("countries");
    Iterator<JSONObject> countryIterator = countries.iterator();
    while (countryIterator.hasNext()) {
      JSONObject country = countryIterator.next();
      List<City> workingCities = new ArrayList<City>();

      // Iterate through cities
      JSONArray cities = (JSONArray) country.get("cities");
      Iterator<JSONObject> cityIterator = cities.iterator();
      while (cityIterator.hasNext()) {
        JSONObject city = cityIterator.next();
        workingCities.add(new City((long) city.get("id"), (String) city.get("name")));
      }

      // Create new country
      Country workingCountry = new Country((String) country.get("name"), workingCities);
      this.countryList.add(workingCountry);
    }
  }

  private void initializeTreeView() {
    // Create tree root element
    TreeItem<ILocation> root = new TreeItem<ILocation>((ILocation) (new RootElement()));

    // Iterate through saved locations
    for (Country country : this.countryList) {
      TreeItem<ILocation> currentCountryItem = new TreeItem<ILocation>(country);
      for (City city : country.getCityList()) {
        currentCountryItem.getChildren().add(new TreeItem<ILocation>(city));
      }
      root.getChildren().add(currentCountryItem);
    }

    // Bind created locations to view
    this.locationTree.setRoot(root);

    // Add click listener
    this.locationTree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener() {
              @Override
              public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<ILocation> selectedItem = (TreeItem<ILocation>) newValue;
                if (selectedItem.isLeaf()) {
                  try {
                    WindowController.getInstance()
                        .changeWeather(selectedItem.getValue().getCityId());
                  } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                  }
                }
              }
            });
  }

  private float tryConvertToFloat(Object object) {
    try {
      return ((Double) object).floatValue();
    } catch (Exception outer) {
      try {
        return ((Long) object).floatValue();
      } catch (Exception inner) {
        return 0;
      }
    }
  }

  public void changeWeather(long cityId) throws IOException, URISyntaxException {
    // Get API key stored into the resources
    Properties configuration = new Properties();
    File configurationFile = new File(getClass().getResource("/sunrise.conf").toURI());
    try {
      configuration.load(new FileInputStream(configurationFile));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    String apiKey = configuration.getProperty("api_key");

    // Create API call
    String apiCall =
        String.format(
            "http://api.openweathermap.org/data/2.5/weather?id=%d&appid=%s&units=metric",
            cityId, apiKey);
    HttpURLConnection con = (HttpURLConnection) (new URL(apiCall)).openConnection();
    con.setRequestMethod("GET");
    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

      // Get JSON representation of the weather conditions
      String respone = new String(con.getInputStream().readAllBytes());

      // Parse
      JSONParser jsonParser = new JSONParser();
      JSONObject rawWeather = null;
      try {
        rawWeather = (JSONObject) jsonParser.parse(respone);
      } catch (ParseException e) {
        e.printStackTrace();
        return;
      }

      // Get picture
      JSONArray workingArrayMember = (JSONArray) rawWeather.get("weather");
      JSONObject workingMember = (JSONObject) workingArrayMember.get(0);
      this.weather.setOverallWeatherPicture((String) workingMember.get("icon"));

      // Get temperature
      workingMember = (JSONObject) rawWeather.get("main");
      this.weather.setTemperature(this.tryConvertToFloat(workingMember.get("temp")));

      // Get wind speed
      workingMember = (JSONObject) rawWeather.get("wind");
      this.weather.setWindSpeed(this.tryConvertToFloat(workingMember.get("speed")));

      // Get cloudiness
      workingMember = (JSONObject) rawWeather.get("clouds");
      this.weather.setCloudiness(this.tryConvertToFloat(workingMember.get("all")));

      // Get precipitations
      float precipitationVolume = 0;
      workingMember = (JSONObject) rawWeather.get("rain");
      if (workingMember != null && workingMember.get("1h") != null)
        precipitationVolume = this.tryConvertToFloat(workingMember.get("1h"));
      workingMember = (JSONObject) rawWeather.get("snow");
      if (workingMember != null && workingMember.get("1h") != null)
        precipitationVolume += this.tryConvertToFloat(workingMember.get("1h"));
      this.weather.setPrecipitationVolume(precipitationVolume);

      // Binding workaround, as it doesn't bind nothing..
      this.weatherPicture.setImage(this.weather.getOverallWeatherPicture());
      this.temperatureLabel.setText(this.weather.getTemperature());
      this.cloudinessLabel.setText(this.weather.getCloudiness());
      this.windSpeedLabel.setText(this.weather.getWindSpeed());
      this.precipitationVolumeLabel.setText(this.weather.getPrecipitationVolume());
    }
  }
}
