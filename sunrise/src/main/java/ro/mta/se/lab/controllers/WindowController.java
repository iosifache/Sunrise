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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
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

  private static class RootElement implements ILocation {
    public String getName() {
      return "Available Countries";
    }

    public String toString() {
      return "Available Countries";
    }

    public Long getCityId() {
      return (long) -1;
    }
  }

  static final String LOCATIONS_RESOURCE_FILENAME = "/locations.json";
  static final String CONFIGURATION_RESOURCE_FILENAME = "/sunrise.conf";
  static final String API_REQUEST_FORMAT_STRING =
      "http://api.openweathermap.org/data/2.5/weather?id=%d&appid=%s&units=metric";

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
      initializeLocationList();
      initializeTreeView();
    } catch (Exception e) {
      e.printStackTrace();
    }
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
    Logger.getGlobal().log(Level.INFO, "Minimized window");
  }

  @FXML
  public void closeWindow(MouseEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
    Logger.getGlobal().log(Level.INFO, "Closed window");
  }

  public void initializeLocationList() {
    // Read the file containing the locations from resources
    JSONParser jsonParser = new JSONParser();
    String jsonFilename = this.getClass().getResource(LOCATIONS_RESOURCE_FILENAME).getPath();
    JSONObject rawLocations = null;
    try {
      rawLocations = (JSONObject) jsonParser.parse(new FileReader(jsonFilename));
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, "Critical error on reading location file");
      System.exit(1);
    }

    // Iterate through counties
    JSONArray countries = (JSONArray) rawLocations.get("countries");
    for (JSONObject country : (Iterable<JSONObject>) countries) {
      List<City> workingCities = new ArrayList<City>();
      JSONArray cities = (JSONArray) country.get("cities");
      Iterator<JSONObject> cityIterator = cities.iterator();
      while (cityIterator.hasNext()) {
        JSONObject city = cityIterator.next();
        workingCities.add(new City((long) city.get("id"), (String) city.get("name")));
      }
      Country workingCountry = new Country((String) country.get("name"), workingCities);
      this.countryList.add(workingCountry);
    }

    Logger.getGlobal().log(Level.INFO, "Success in locations initialization");
  }

  private void initializeTreeView() {
    // Iterate through saved locations
    TreeItem<ILocation> root = new TreeItem<ILocation>((ILocation) (new RootElement()));
    for (Country country : this.countryList) {
      TreeItem<ILocation> currentCountryItem = new TreeItem<ILocation>(country);
      for (City city : country.getCityList()) {
        currentCountryItem.getChildren().add(new TreeItem<ILocation>(city));
      }
      root.getChildren().add(currentCountryItem);

      // Add expand listener
      currentCountryItem
          .expandedProperty()
          .addListener(
              new ChangeListener<Boolean>() {
                @Override
                public void changed(
                    ObservableValue<? extends Boolean> observable,
                    Boolean oldValue,
                    Boolean newValue) {
                  if (newValue) {
                    BooleanProperty bb = (BooleanProperty) observable;
                    TreeItem<ILocation> selectedItem = (TreeItem<ILocation>) bb.getBean();
                    Logger.getGlobal()
                        .log(
                            Level.INFO,
                            "New country selected, namely " + selectedItem.getValue().getName());
                  }
                }
              });
    }

    // Set the root containing all locations to the view
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
                    Logger.getGlobal()
                        .log(
                            Level.INFO,
                            "New city selected, namely " + selectedItem.getValue().getName());
                    WindowController.getInstance()
                        .changeWeather(selectedItem.getValue().getCityId());
                  } catch (Exception e) {
                    Logger.getGlobal().log(Level.SEVERE, "Critical error on item selection");
                    System.exit(1);
                  }
                }
              }
            });

    Logger.getGlobal().log(Level.INFO, "Success in location tree initialization");
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
    // Get API key stored in resources
    Properties configuration = new Properties();
    File configurationFile =
        new File(getClass().getResource(CONFIGURATION_RESOURCE_FILENAME).toURI());
    try {
      configuration.load(new FileInputStream(configurationFile));
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Critical error on reading the configuration file");
      System.exit(1);
    }
    String apiKey = configuration.getProperty("api_key");
    if (apiKey == null) {
      Logger.getGlobal().log(Level.SEVERE, "Critical error on reading the API key");
      System.exit(1);
    }

    // Create API call
    String apiCall = String.format(API_REQUEST_FORMAT_STRING, cityId, apiKey);
    HttpURLConnection con = (HttpURLConnection) (new URL(apiCall)).openConnection();
    con.setRequestMethod("GET");
    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
      String response = new String(con.getInputStream().readAllBytes());

      // Parse
      JSONParser jsonParser = new JSONParser();
      JSONObject rawWeather = null;
      try {
        rawWeather = (JSONObject) jsonParser.parse(response);
      } catch (ParseException e) {
        Logger.getGlobal().log(Level.FINER, "Error on parsing the weather data");
        return;
      }

      // Get picture
      JSONArray workingArrayMember = (JSONArray) rawWeather.get("weather");
      JSONObject workingMember = (JSONObject) workingArrayMember.get(0);
      this.weather.setPictureId((String) workingMember.get("icon"));

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
      this.weatherPicture.setImage(this.weather.getPictureId());
      this.temperatureLabel.setText(this.weather.getTemperature());
      this.cloudinessLabel.setText(this.weather.getCloudiness());
      this.windSpeedLabel.setText(this.weather.getWindSpeed());
      this.precipitationVolumeLabel.setText(this.weather.getPrecipitationVolume());

      Logger.getGlobal().log(Level.INFO, "New weather displayed:\n" + this.weather.toString());
    }
  }
}
