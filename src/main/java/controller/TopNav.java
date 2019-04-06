package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.*;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;


public class TopNav {

    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private JFXButton navigate_btn, fulfillBtn, auth_btn, schedulerBtn, edit_btn;
    @FXML
    private JFXTextField search_bar ;

    // events I send out/control
    @FXML
    void showAdminLogin(ActionEvent e) throws Exception {
        if (event.isAdmin()) {
            Event sendEvent = new Event();
            sendEvent.setEventName("login");
            eventBus.post(sendEvent);
            resetBtn();

        } else {
            Parent root = FXMLLoader.load(ResourceLoader.adminLogin);
            Stage stage = (Stage) navigate_btn.getScene().getWindow();
            StageManager.changeExistingWindow(stage, root, "Admin Login");
        }
    }




    @FXML
    // switches window to map editor screen.
    public void showFulfillRequest() throws Exception {
        Stage stage = (Stage) fulfillBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Fulfill Service Request");
    }

    @FXML
    void showSchedule(ActionEvent e) throws Exception {
        Stage stage = (Stage) schedulerBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeExistingWindow(stage, root, "Scheduler");
        stage.sizeToScene();
        stage.setFullScreen(true);
    }


    @FXML
    void showRequest(ActionEvent e) throws Exception {
        Stage stage = (Stage) navigate_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request);
        StageManager.changeExistingWindow(stage, root, "Service Request");
    }

    @FXML
    void initialize() {
        eventBus.register(this);

        // SHOULD THIS GO HERE? (was in intialize of old map controller)
        navigate_btn.setVisible(false);

        resetBtn();

    }

    void handleFindPath() {

    }


    // events I care about: am "subscribed" to
    @Subscribe
    private void eventListener(Event newEvent) {

        switch (newEvent.getEventName()) {
            case "node-select":
                event.setNodeSelected(newEvent.getNodeSelected());
                event.getNodeSelected();
                // show navigation button
                // navigate_btn.setVisible(true);
                //showNavigationBtn(event);
                showNavigationBtn(newEvent.getNodeSelected());        // will make nav btn visible, fill search with node
                break;

            case "login":     // receives from AdminLoginContoller?
                event.setAdmin(newEvent.isAdmin());
                break;
        }

    }

    private void resetBtn() {
        if(event.isAdmin()){
            fulfillBtn.setVisible(true);
            edit_btn.setVisible(true);
        } else {
            fulfillBtn.setVisible(false);
            edit_btn.setVisible(false);
        }
    }


    /**
     * searches for room
     * @param e
     */
    @FXML
    public void searchBarEnter(ActionEvent e) {
        String search = search_bar.getText();

        Event sendEvent = new Event();
        sendEvent.setSearchBarQuery(search);
        sendEvent.setEventName("search-query");
        eventBus.post(sendEvent);
    }

    // when event comes in with a node-selected:
    //      show navigation button
    //      show node-selected in search
    @FXML
    void showNavigationBtn(Node selected) {
        // create new event
        //event.setEventName("navigate-btn-on");
        // make change
        navigate_btn.setVisible(true);
        // share
        //eventBus.post(event);   -- dont need to alert if visible, just if is selected

        // show node-selected in search
        String fillNodeinSearch = selected.getLongName();
        search_bar.setText(fillNodeinSearch);

    }

    public void startNavigation(ActionEvent actionEvent) {
    }
}
