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
import model.Event;
import model.EventBusFactory;
import model.HomeState;
import model.LoginEvent;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;


public class TopNav {

    private Event event = new Event();
    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private JFXButton navigate_btn, fulfillBtn, auth_btn, schedulerBtn, edit_btn;
    private JFXTextField search_bar ;

    // events I send out/control
    @FXML
    void showAdminLogin(ActionEvent e) throws Exception {
        Parent root = FXMLLoader.load(ResourceLoader.adminLogin);
        Stage stage = (Stage) auth_btn.getScene().getWindow();
        StageManager.changeExistingWindow(stage, root, "Admin Login");
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

        resetBtn(false);

    }

    void handleFindPath() {

    }


    // events I care about: am "subscribed" to
    @Subscribe
    private void eventListener(Event event) {

        switch (event.getEventName()) {
            case "node-select":
                this.event.setNodeSelected(event.getNodeSelected());
                event.getNodeSelected();
                // show navigation button
                // navigate_btn.setVisible(true);
                //showNavigationBtn(event);
                showNavigationBtn();        // will make nav btn visible, fill search with node
                System.out.println("search result received search");
                break;

            case "login":     // receives from AdminLoginContoller?
                this.event.setLoggedIn(event.isLoggedIn());
                this.event.setAdmin(event.isAdmin());
                if(event.isAdmin()){
                    resetBtn(true);
                }
                break;
        }
    }

    private void resetBtn(boolean isAdmin) {
        if(isAdmin){
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
        event.setSearchBarQuery(search);
        event.setEventName("search-query");
        eventBus.post(event);
    }

    // when event comes in with a node-selected:
    //      show navigation button
    //      show node-selected in search
    @FXML
    void showNavigationBtn() { // but how do i do this for node-selected?
        // create new event
        //event.setEventName("navigate-btn-on");
        // make change
        navigate_btn.setVisible(true);
        // share
        //eventBus.post(event);   -- dont need to alert if visible, just if is selected

        // show node-selected in search
        String fillNodeinSearch = event.getNodeSelected().getLongName(); // change to short name?
        search_bar.setText(fillNodeinSearch);

    }

}
