package home;

import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.JFXToggleNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import application_state.Event;
import application_state.EventBusFactory;

public class PathSettingController {

    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private JFXToggleNode accessibilityButton;

    @FXML
    void showSearchResults(ActionEvent e) {
        event.setEventName("showSearch");
        eventBus.post(event);
    }

    public void showREST(ActionEvent actionEvent) {
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("REST");
        eventBus.post(event);
    }


    public void showELEV(ActionEvent actionEvent) {
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("ELEV");
        eventBus.post(event);
    }

    public void showSTAI(ActionEvent actionEvent) {
        event.setEventName("filter");
        event.setFilterSearch("STAI");
        eventBus.post(event);
    }

    public void showINFO(ActionEvent actionEvent) {
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("INFO");
        eventBus.post(event);
    }

    public void showCONF(ActionEvent actionEvent) {
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("CONF");
        eventBus.post(event);
    }

    public void showEXIT(ActionEvent actionEvent) {
        Boolean accessibility = accessibilityButton.isSelected();
        event.setAccessiblePath(accessibility);
        event.setEventName("filter");
        event.setFilterSearch("EXIT");
        eventBus.post(event);
    }

    public void switchAccessibility(ActionEvent actionEvent) {
        if (event.isAccessiblePath()){
            event.setAccessiblePath(false);
        } else {
            event.setAccessiblePath(true);
        }
    }
}
