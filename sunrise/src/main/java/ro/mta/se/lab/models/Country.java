package ro.mta.se.lab.models;

import java.util.ArrayList;
import java.util.List;

public class Country implements ILocation {

  private String name;
  private List<City> cityList = new ArrayList<City>();

  public Country(String name, List<City> cityList) {
    this.name = name;
    this.cityList = cityList;
  }

  public String getName() {
    return name;
  }

  public List<City> getCityList() {
    return cityList;
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public Long getCityId() {
    return (long) -1;
  }
}
