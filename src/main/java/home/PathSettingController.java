package home;

import application_state.ApplicationState;
import com.jfoenix.controls.JFXToggleNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import application_state.Event;

public class PathSettingController {

    private Event event = ApplicationState.getApplicationState().getObservableBus().getEvent();

    @FXML
    private JFXToggleNode accessibilityButton;

    @FXML
    void initialize() {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        accessibilityButton.setSelected(event.isAccessiblePath());
    }

    @FXML
    void showSearchResults(ActionEvent e) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("closeDrawer");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    public void showREST(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("REST");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }


    public void showELEV(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("ELEV");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    public void showSTAI(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setEventName("filter");
        event.setFilterSearch("STAI");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    public void showINFO(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("INFO");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    public void showCONF(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("CONF");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

    public void showEXIT(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("EXIT");
        ApplicationState.getApplicationState().getObservableBus().updateEvent(event);
    }

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
