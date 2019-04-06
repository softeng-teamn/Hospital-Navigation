package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleNode;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
    private JFXButton navigate_btn, fulfillBtn, auth_btn, bookBtn, edit_btn, newNode_btn;
    @FXML
    private JFXTextField search_bar ;
    @FXML
    private JFXToggleNode accessibilityButton;
    @FXML
    private FontAwesomeIconView lock_icon;

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
    void showNewNode(ActionEvent e) throws  Exception{
        Parent root = FXMLLoader.load(ResourceLoader.createNode);
        Stage stage = (Stage) newNode_btn.getScene().getWindow();
        StageManager.changeExistingWindow(stage, root, "Add Node");
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
        Stage stage = (Stage) bookBtn.getScene().getWindow();
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
        accessibilityButton.setVisible(false);

        resetBtn();

    }



    // events I care about: am "subscribed" to
    @Subscribe
    private void eventListener(Event newEvent) {

        switch (newEvent.getEventName()) {
            case "node-select":
                event.setNodeSelected(newEvent.getNodeSelected());
                // show navigation button
                // navigate_btn.setVisible(true);
                //showNavigationBtn(event);
                nodeSelectedHandler(newEvent.getNodeSelected());        // will make nav btn visible, fill search with node
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
            newNode_btn.setVisible(true);
            lock_icon.setIcon(FontAwesomeIcon.SIGN_OUT);
        } else {
            fulfillBtn.setVisible(false);
            edit_btn.setVisible(false);
            newNode_btn.setVisible(false);
            lock_icon.setIcon(FontAwesomeIcon.SIGN_IN);
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
    void nodeSelectedHandler(Node selected) {
        // make change
        navigate_btn.setVisible(true);
        accessibilityButton.setVisible(true);


        // show node-selected in search
        String fillNodeinSearch = selected.getLongName();
        search_bar.setText(fillNodeinSearch);

    }

    public void startNavigation(ActionEvent actionEvent) {
        Event sendEvent = new Event();
        Boolean accessibility = accessibilityButton.isSelected();
        sendEvent.setNodeSelected(event.getNodeSelected());
        sendEvent.setAccessiblePath(accessibility);
        sendEvent.setEventName("navigation");
        eventBus.post(sendEvent);
    }
}
