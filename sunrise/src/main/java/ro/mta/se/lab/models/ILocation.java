package ro.mta.se.lab.models;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public interface ILocation {
  String getName();

  String toString();

  Long getCityId() throws NotImplementedException;
}
