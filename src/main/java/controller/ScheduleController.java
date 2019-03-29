package controller;

public class ScheduleController extends Controller {

    @FXML
    private JFXButton homeBtn;

    // switches window to home screen
    private void showHome() {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeWindow(stage, root, "Home (Path Finder)");
    }

}
