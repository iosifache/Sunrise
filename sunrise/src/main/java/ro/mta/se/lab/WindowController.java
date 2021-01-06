package ro.mta.se.lab;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class WindowController {

  double x;
  double y;

  @FXML
  private TreeView<String> locationTree;

  public void initLocationList(){
    // Create each country and their cities
    TreeItem<String> root = new TreeItem<String>("Country");
    root.getChildren().add(new TreeItem<String>("City #1"));
    root.getChildren().add(new TreeItem<String>("City #2"));

    // Bind created locations to view
    locationTree.setRoot(root);
  }

  @FXML
  public void initialize(){
    initLocationList();
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
