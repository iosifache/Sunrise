package ro.mta.se.lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.stage.StageStyle;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage primaryStage) {
    FXMLLoader loader = new FXMLLoader();
    try {
      loader.setLocation(this.getClass().getResource("/view/MainView.fxml"));
      primaryStage.setScene(new Scene(loader.load()));
      primaryStage.initStyle(StageStyle.TRANSPARENT);
      primaryStage.setResizable(false);
      primaryStage.show();
    } catch (IOException e) {
      System.exit(1);
    }
  }
}
