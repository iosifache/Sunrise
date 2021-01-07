package ro.mta.se.lab.models;

public class City implements ILocation {

  private Long id;
  private String name;

  public City(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Long getCityId() {
    return id;
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
