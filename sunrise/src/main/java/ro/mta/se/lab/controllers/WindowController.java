package ro.mta.se.lab.controllers;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ro.mta.se.lab.models.City;
import ro.mta.se.lab.models.Country;
import ro.mta.se.lab.models.ILocation;

public class WindowController {

  private class RootElement implements ILocation{

    public String getName(){
      return "Available Countries";
    };

    public String toString(){
      return "Available Countries";
    };

    public Long getCityId(){
      return (long) -1;
    };

  }

  private List<Country> countryList = new ArrayList<Country>();
  double x;
  double y;

  @FXML
  private TreeView<ILocation> locationTree;

  public void initializeLocationList() throws IOException, ParseException {
    // Parse JSON file with locations
    JSONParser jsonParser = new JSONParser();
    String jsonFilename = this.getClass().getResource("/locations.json").getPath();
    JSONObject rawLocations = (JSONObject) jsonParser.parse(new FileReader(jsonFilename));

    // Iterate through counties
    JSONArray countries = (JSONArray) rawLocations.get("countries");
    Iterator<JSONObject> countryIterator = countries.iterator();
    while(countryIterator.hasNext()) {
      JSONObject country = countryIterator.next();
      List<City> workingCities = new ArrayList<City>();

      // Iterate through cities
      JSONArray cities = (JSONArray) country.get("cities");
      Iterator<JSONObject> cityIterator = cities.iterator();
      while(cityIterator.hasNext()) {
        JSONObject city = cityIterator.next();
        workingCities.add(new City((long) city.get("id"), (String) city.get("name")));
      }

      // Create new country
      Country workingCountry = new Country((String)country.get("name"), workingCities);
      this.countryList.add(workingCountry);
    }

  }

  private void initializeTreeView(){
    // Create tree root element
    TreeItem<ILocation> root = new TreeItem<ILocation>((ILocation)(new RootElement()));

    // Iterate through saved locations
    for (Country country : this.countryList){
      TreeItem<ILocation> currentCountryItem = new TreeItem<ILocation>(country);
      for (City city : country.getCityList()){
        currentCountryItem.getChildren().add(new TreeItem<ILocation>(city));
      }
      root.getChildren().add(currentCountryItem);
    }

    // Bind created locations to view
    this.locationTree.setRoot(root);

    // Add click listener
    this.locationTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        TreeItem<ILocation> selectedItem = (TreeItem<ILocation>) newValue;
        if (selectedItem.isLeaf()){
          Long selectedId = selectedItem.getValue().getCityId();
        }
      }
    });
  }

  @FXML
  public void initialize(){
    try {
      initializeLocationList();
      initializeTreeView();
    } catch (Exception e) {
      e.printStackTrace();
    };
  }

  @FXML
  public void clickWindow(javafx.scene.input.MouseEvent event) {
    this.x = event.getSceneX();
    this.y = event.getSceneY();
  }

  @FXML
  public void dragWindow(javafx.scene.input.MouseEvent event) {
    Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    stage.setX(event.getScreenX() - this.x);
    stage.setY(event.getScreenY() - this.y);
  }

  @FXML
  public void minimizeWindow(MouseEvent event) {
    Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    stage.setIconified(true);
  }

  @FXML
  public void closeWindow(MouseEvent event) {
    Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    stage.close();
  }

}
