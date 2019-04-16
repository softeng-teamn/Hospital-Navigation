package home;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXToggleNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import application_state.Event;

/**
 * Path drawing settings
 */
public class PathSettingController {

    private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();

    @FXML
    private JFXToggleNode accessibilityButton;

    /**
     * initializes the FXML
     */
    @FXML
    void initialize() {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        accessibilityButton.setSelected(event.isAccessiblePath());
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
        //ApplicationState.getApplicationState().getObservableBus().updateEvent(event); // todo should this be here? I added it
    }
}
