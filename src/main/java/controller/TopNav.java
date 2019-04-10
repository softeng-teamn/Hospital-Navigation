package controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class TopNav {

    public HBox top_nav;
    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private JFXButton navigate_btn, fulfillBtn, auth_btn, bookBtn, newNode_btn, startNode_btn;    // TODO: rename fulfillbtn and change icon
    @FXML
    private JFXTextField search_bar ;
    @FXML
    private JFXToggleNode accessibilityButton;
    @FXML
    private FontAwesomeIconView lock_icon;
    @FXML
    private MaterialIconView home_icon;
    @FXML
    private Label time_label;
    @FXML
    private JFXToggleNode edit_btn;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXCheckBox callElev;


    JFXTextField startSearch = new JFXTextField();

    // events I send out/control
    @FXML
    void showAdminLogin(ActionEvent e) throws Exception {
        if (event.isAdmin()) {
            event.setAdmin(false);
            event.setLoggedIn(false);
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
    public void showAdminScene() throws Exception {
        Stage stage = (Stage) fulfillBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.adminServices);
        StageManager.changeExistingWindow(stage, root, "Administrator Services");
    }

    @FXML
    void showSchedule(ActionEvent e) throws Exception {
        Stage stage = (Stage) bookBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.scheduler);
        StageManager.changeExistingWindow(stage, root, "Scheduler");
        stage.setMaximized(true);
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

        // Turn off editing
        event.setEventName("editing");
        event.setEditing(false);
        eventBus.post(event);

        // SHOULD THIS GO HERE? (was in intialize of old map controller)
        navigate_btn.setVisible(false);
        accessibilityButton.setVisible(false);

        resetBtn();

        // set Default time
        timeWatcher();

        HamburgerBackArrowBasicTransition backArrow = new HamburgerBackArrowBasicTransition(hamburger);
        backArrow.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            backArrow.setRate(backArrow.getRate()*-1);
            backArrow.play();
        });
    }

    private void timeWatcher() {
        time_label.setTextFill(Color.WHITE);
        Task task = new Task<Void>() {
            @Override public Void call() throws Exception {
                while (true) {
                    Thread.sleep(100);
                    GregorianCalendar calendar = new GregorianCalendar();
                    TimeUnit.SECONDS.sleep(1);
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            time_label.setText(calendar.getTime().toString());
                        }
                    });
                }
            }
        };

        new Thread(task).start();
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
                if (event.isEndNode()){
                    nodeSelectedHandler(newEvent.getNodeSelected());        // will make nav btn visible, fill search with node
                } else {
                    nodeSelectedHandler(newEvent.getNodeStart());
                }
                break;

            case "login":     // receives from AdminLoginContoller?
                event.setAdmin(newEvent.isAdmin());
                break;
            default:
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

    @FXML
    public void editButtonAction(ActionEvent e) throws Exception {
        event.setEventName("editing");
        event.setEditing(!event.isEditing());
        System.out.println("Editing: " + event.isEditing());
        eventBus.post(event);
    }

    /**
     * searches for room
     * @param e
     */
    @FXML
    public void startNodeEnter(ActionEvent e) {
        String search = startSearch.getText();

        event.setSearchBarQuery(search);
        event.setEventName("search-query");
        event.setEndNode(false);
        eventBus.post(event);
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
        event.setEndNode(true);
        eventBus.post(event);
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

        if(event.isEndNode()){
            search_bar.setText(fillNodeinSearch);
        } else {
            startSearch.setText(fillNodeinSearch);
        }
    }

    public void startNavigation(ActionEvent actionEvent) {
        Boolean accessibility = accessibilityButton.isSelected();
        //if(callElev.isSelected()){
            event.setCallElev(true);
        //}
        event.setAccessiblePath(accessibility);
        event.setEventName("navigation");
        eventBus.post(event);
    }


    public void setEventEndNode(MouseEvent mouseEvent){
        event.setEndNode(false);
    }


    public void setEventStartNode(MouseEvent mouseEvent) {
        event.setEndNode(true);
    }

    @FXML
    public void showStartSearch(ActionEvent actionEvent) {
        if (startNode_btn.getText().equals("Start Node")){
            startSearch.setPromptText("Start Node");
            startSearch.setOnAction(this::startNodeEnter);
            startSearch.setOnMouseClicked(this::setEventEndNode);
            startSearch.getStyleClass().add("header-text-field");
            top_nav.getChildren().add(2, startSearch);
            event.setEndNode(false);
            startNode_btn.setText("Use default");
            home_icon.setIcon(MaterialIcon.ARROW_BACK);
        }
        else {
            top_nav.getChildren().remove(startSearch);
            event.setEndNode(true);
            event.setDefaultStartNode();
            event.setEventName("refresh");
            eventBus.post(event);
            startNode_btn.setText("Start Node");
            home_icon.setIcon(MaterialIcon.LOCATION_ON);
        }
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

    public void showEditEmployee(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) auth_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.employeeEdit);
        StageManager.changeExistingWindow(stage, root, "Edit Employees");
        stage.setFullScreen(true);

    }

}
