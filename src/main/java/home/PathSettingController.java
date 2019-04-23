package home;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleNode;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import application_state.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

/**
 * Path drawing settings
 */
public class PathSettingController {

    private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();

    @FXML
    private JFXToggleNode accessibilityButton;

    @FXML
    private JFXComboBox<String> theme;

    /**
     * initializes the FXML
     */
    @FXML
    void initialize() {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        accessibilityButton.setSelected(event.isAccessiblePath());
        theme.setItems(FXCollections.observableArrayList("Default", "High-Contrast", "Night", "Iteration 1", "Neon Narwhal"));
        //theme.getSelectionModel().select(0);
    }

    /** controls the search results bar
     * @param e FXML event that calls this method
     */
    @FXML
    void showSearchResults(ActionEvent e) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("closeDrawer");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** shows restrooms in the search bar
     * @param actionEvent FXML event that calls this method
     */
    public void showREST(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("REST");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }


    /** shows elevators in the search bar
     * @param actionEvent FXML event that calls this method
     */
    public void showELEV(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("ELEV");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** shows stairs in the search bar
     * @param actionEvent FXML event that calls this method
     */
    public void showSTAI(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("filter");
        event.setFilterSearch("STAI");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** shows information centers in the search bar
     * @param actionEvent FXML event that calls this method
     */
    public void showINFO(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("INFO");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** shows conference rooms in the search bar
     * @param actionEvent FXML event that calls this method
     */
    public void showCONF(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("CONF");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** shows exits in the search bar
     * @param actionEvent FXML event that calls this method
     */
    public void showEXIT(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("EXIT");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    /** Switches the state of accessibility mode
     * @param actionEvent FXML event that calls this method
     */
    public void switchAccessibility(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        if (event.isAccessiblePath()){
            event.setAccessiblePath(false);
            accessibilityButton.setSelected(false);
        } else {
            event.setAccessiblePath(true);
            accessibilityButton.setSelected(true);
        }
    }

    /** changes the current theme in use by the application
     * @param actionEvent FXML event that calls this method
     */
    public void switchTheme(ActionEvent actionEvent) throws Exception{
        // get the selected theme
        String newTheme = theme.getSelectionModel().getSelectedItem();
        if(newTheme.equals("Default")){
            ApplicationState.getApplicationState().setCurrentTheme(ResourceLoader.default_style);
        } else if(newTheme.equals("High-Contrast")) {
            ApplicationState.getApplicationState().setCurrentTheme(ResourceLoader.high_contrast_style);
        } else if (newTheme.equals("Night")){
            ApplicationState.getApplicationState().setCurrentTheme(ResourceLoader.night_style);
        } else if(newTheme.equals("Iteration 1")){
            ApplicationState.getApplicationState().setCurrentTheme(ResourceLoader.vegas_casino_style);
        } else if(newTheme.equals("Neon Narwhal")){
            ApplicationState.getApplicationState().setCurrentTheme(ResourceLoader.narwhal_style);
        }
        Stage stage = (Stage) accessibilityButton.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

}
