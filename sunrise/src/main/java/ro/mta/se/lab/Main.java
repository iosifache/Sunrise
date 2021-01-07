package ro.mta.se.lab;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.stage.StageStyle;

public class Main extends Application {

  static final String LOG_FILENAME = "log.txt";
  static final String VIEW_RESOURCE_FILENAME = "/view/WindowView.fxml";

  private static void setupLogger() {
    Logger logger = Logger.getGlobal();
    try {
      Handler fileHandler = new FileHandler(LOG_FILENAME);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
      logger.setUseParentHandlers(false);
      logger.addHandler(fileHandler);
    } catch (IOException e) {
      e.printStackTrace();
    }

    logger.log(Level.INFO, "Initialized logger");
  }

  public void start(Stage primaryStage) {
    FXMLLoader loader = new FXMLLoader();
    try {
      loader.setLocation(this.getClass().getResource(VIEW_RESOURCE_FILENAME));
      primaryStage.setScene(new Scene(loader.load()));
      primaryStage.setResizable(false);
      primaryStage.initStyle(StageStyle.TRANSPARENT);
      primaryStage.show();
    } catch (IOException e) {
      Logger logger = Logger.getGlobal();
      logger.log(Level.SEVERE, "Critical error on creating user interface");
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    Main.setupLogger();
    Main.launch(args);
  }
}
