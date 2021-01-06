package ro.mta.se.lab.models;

public class City implements ILocation {

  Long id;
  String name;

  public City(long id, String name){
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public Long getCityId() {
    return id;
  }

}
