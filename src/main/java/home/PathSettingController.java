package home;

import application_state.ApplicationState;
import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.JFXToggleNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import application_state.Event;

public class PathSettingController {

    private Event event = ApplicationState.getApplicationState().getFeb().getEvent();

    @FXML
    private JFXToggleNode accessibilityButton;

    @FXML
    void showSearchResults(ActionEvent e) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        event.setEventName("showSearch");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    public void showREST(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("REST");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }


    public void showELEV(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("ELEV");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    public void showSTAI(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        event.setEventName("filter");
        event.setFilterSearch("STAI");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    public void showINFO(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("INFO");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    public void showCONF(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("CONF");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    public void showEXIT(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("EXIT");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    public void switchAccessibility(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        if (event.isAccessiblePath()){
            event.setAccessiblePath(false);
        } else {
            event.setAccessiblePath(true);
        }
        ApplicationState.getApplicationState().getFeb().updateEvent(event); // todo should this be here? I added it
    }
}
