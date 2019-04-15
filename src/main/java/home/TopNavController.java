package home;

import application_state.ApplicationState;
import application_state.Observer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import database.DatabaseService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import application_state.Event;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import map.Node;
import service.ResourceLoader;
import service.StageManager;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class TopNavController implements Observer {

    public HBox top_nav;
    private Event event;

    @FXML
    private JFXButton navigate_btn, fulfillBtn, auth_btn, bookBtn, startNode_btn, requestBtn;
    @FXML
    private JFXTextField search_bar ;
    @FXML
    private FontAwesomeIconView lock_icon;
    @FXML
    private MaterialIconView home_icon;
    @FXML
    private Label time_label;
    @FXML
    private JFXToggleNode edit_btn, accessibilityButton;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXCheckBox callElev;

    private boolean barOpened = false;

    JFXTextField startSearch = new JFXTextField();
    HamburgerBackArrowBasicTransition backArro;
    private HashMap<String, Node> nodeLongNames = new HashMap<>();


    @FXML
    void showAdminLogin(ActionEvent e) throws Exception {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        // when admin or employee logs out
        if (event.isAdmin() || event.isLoggedIn()) {
            event.setAdmin(false);
            event.setLoggedIn(false);
            event.setEventName("logout");
            ApplicationState.getApplicationState().getFeb().updateEvent(event);
            resetBtn();
            ApplicationState.getApplicationState().setEmployeeLoggedIn(null);
        }
        // go to login screen
        else {
            Parent root = FXMLLoader.load(ResourceLoader.adminLogin);
            Stage stage = (Stage) navigate_btn.getScene().getWindow();
            StageManager.changeExistingWindow(stage, root, "Admin Login");
        }
    }

    @FXML
    // switches window to map editor screen.
    public void showAdminScene() throws Exception {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        event.setEventName("showAdmin");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
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
        Parent root = FXMLLoader.load(ResourceLoader.request,event.getCurrentBundle());
        StageManager.changeExistingWindow(stage,root,"Service Request");
    }

    @FXML
    void initialize() {
        ApplicationState.getApplicationState().getFeb().register("topNavController", this);

        // Turn off editing
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        event.setEventName("editing");
        event.setEditing(false);
        ApplicationState.getApplicationState().getFeb().updateEvent(event);

        navigate_btn.setVisible(false);

        resetBtn();

        // set Default time
        timeWatcher();

        HamburgerBackArrowBasicTransition backArrow = new HamburgerBackArrowBasicTransition(hamburger);
        backArrow.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            backArrow.setRate(backArrow.getRate()*-1);
            backArrow.play();
        });

        backArro = backArrow;

        ArrayList<Node> allNodes = DatabaseService.getDatabaseService().getAllNodes();
        for (Node n: allNodes) {
            nodeLongNames.put(n.getLongName(), n);
        }
    }

    private void timeWatcher() {
        time_label.setTextFill(Color.WHITE);
        Task task = new Task<Void>() {
            @Override public Void call() throws Exception {
                while (true) {
                    Thread.sleep(100);
                    GregorianCalendar calendar = new GregorianCalendar();
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    TimeUnit.SECONDS.sleep(1);
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            String formattedDate = dateFormat.format(new Date());
                            String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                            int date = calendar.get(Calendar.DAY_OF_MONTH);
                            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                            String curTime = String.format(formattedDate + "\n" + day + ", the %02dth of " + month, date);
                            time_label.setText(curTime);
                            time_label.setTextAlignment(TextAlignment.CENTER);
                        }
                    });
                }
            }
        };

        new Thread(task).start();
    }

    // events I care about: am "subscribed" to
    @Override
    public void notify(Object newEvent) {
        event = (Event) newEvent;
        ApplicationState currState = ApplicationState.getApplicationState();
        switch (event.getEventName()) {
            case "node-select-end":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(" \nSTART: " + currState.getStartNode() + "end: " + currState.getEndNode() + "\nselected: " + event.getNodeSelected());
                        currState.setEndNode(event.getNodeSelected());
                        search_bar.setText(currState.getEndNode().getLongName());
                        if (currState.getEndNode() != null && currState.getStartNode() != null) {
                            navigate_btn.setVisible(true);
                        }
                        else {
                            navigate_btn.setVisible(false);
                        }
                        System.out.println(" \nSTART: " + currState.getStartNode() + "end: " + currState.getEndNode() + "\nselected: " + event.getNodeSelected());
                    }
                });
                break;
            case "node-select-start":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(" \nSTART: " + currState.getStartNode() + "end: " + currState.getEndNode() + "\nselected: " + event.getNodeSelected());
                        currState.setStartNode(event.getNodeSelected());
                        startSearch.setText(currState.getStartNode().getLongName());
                        if (currState.getEndNode() != null && currState.getStartNode() != null) {
                            navigate_btn.setVisible(true);
                        }
                        else {
                            navigate_btn.setVisible(false);
                        }
                        System.out.println(" \nSTART: " + currState.getStartNode() + "end: " + currState.getEndNode() + "\nselected: " + event.getNodeSelected());
                    }
                });
                break;
            case "login":     // receives from AdminLoginContoller?
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        event.setAdmin(event.isAdmin());
                    }
                });
                break;
            // remove if way off base
            case "empLogin":
               // event.setLoggedIn((newEvent.isLoggedIn())); todo: what's the point of this code?
                break ;
            case "closeDrawer":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        backArro.setRate(-1);
                        backArro.play();
                        barOpened = false;
                    }
                });
                break;
            default:
                break;
        }

    }

    private void resetBtn() {

        // check why not entering if statment below
        System.out.println("isAdmin = " + event.isAdmin()) ;
        System.out.println("isLoggedIn = " + event.isLoggedIn());


        // if admin is logged in
        if(event.isAdmin()){
            fulfillBtn.setVisible(true);
            edit_btn.setVisible(true);
            lock_icon.setIcon(FontAwesomeIcon.SIGN_OUT);
            bookBtn.setVisible(true);
            requestBtn.setVisible(true);
        }
        // if employee is logged in
        else if ((event.isAdmin() == false) && (event.isLoggedIn() == true)) {
            System.out.println("USER IS AN EMPLOYEE");
            fulfillBtn.setVisible(false);
            edit_btn.setVisible(false);
            lock_icon.setIcon(FontAwesomeIcon.SIGN_OUT);
            bookBtn.setVisible(true);
            requestBtn.setVisible(true);
        }
        // no one is logged in
        else {
            fulfillBtn.setVisible(false);
            edit_btn.setVisible(false);
            lock_icon.setIcon(FontAwesomeIcon.SIGN_IN);
            bookBtn.setVisible(false);
            requestBtn.setVisible(false);
        }
    }

    @FXML
    public void editButtonAction(ActionEvent e) throws Exception {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        event.setEventName("editing");
        event.setEditing(!event.isEditing());
        System.out.println("Editing: " + event.isEditing());
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    /**
     * searches for room
     * @param e
     */
    @FXML
    public void startNodeEnter(javafx.event.Event e) {
        String search = startSearch.getText();
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        ApplicationState currState = ApplicationState.getApplicationState();

        if (nodeLongNames.get(search) == null) {
            currState.setStartNode(null);
            navigate_btn.setVisible(false);
        }

        event.setSearchBarQuery(search);
        event.setEventName("search-query");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    /**
     * searches for room
     */
    @FXML
    public void searchBarEnter() {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        ApplicationState currState = ApplicationState.getApplicationState();
        String search = search_bar.getText();

        if (nodeLongNames.get(search) == null) {
            currState.setEndNode(null);
            navigate_btn.setVisible(false);
        }

        event.setSearchBarQuery(search);
        event.setEventName("search-query");
        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }

    public void startNavigation(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        ApplicationState currState = ApplicationState.getApplicationState();
        //if(callElev.isSelected()){
            event.setCallElev(true);
        //}

        event.setEventName("navigation");

        startSearch.setText(currState.getStartNode().getLongName());
        search_bar.setText(currState.getEndNode().getLongName());

        ApplicationState.getApplicationState().getFeb().updateEvent(event);
    }


    public void setEventEndNode(MouseEvent mouseEvent){
        event = ApplicationState.getApplicationState().getFeb().getEvent();

        event.setEventName("showSearch-end");

        ApplicationState.getApplicationState().getFeb().updateEvent(event);

        if(backArro.getRate() == 1) {
            backArro.setRate(backArro.getRate() * -1);
            backArro.play();
            barOpened = false;
        }
    }


    public void setEventStartNode(MouseEvent mouseEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();

        event.setEventName("showSearch-start");

        ApplicationState.getApplicationState().getFeb().updateEvent(event);

        if(backArro.getRate() == 1) {
            backArro.setRate(backArro.getRate() * -1);
            backArro.play();
            barOpened = false;
        }
    }

    @FXML
    public void showStartSearch(ActionEvent actionEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        ApplicationState currState = ApplicationState.getApplicationState();
        if (startNode_btn.getText().equals("Start Node")){
            event = ApplicationState.getApplicationState().getFeb().getEvent();
            startSearch.setPromptText("Start Node");
            startSearch.setOnInputMethodTextChanged(this::startNodeEnter);
            startSearch.setOnKeyReleased(this::startNodeEnter);
            startSearch.setOnMouseClicked(this::setEventStartNode);
            startSearch.getStyleClass().add("header-text-field");
            top_nav.getChildren().add(2, startSearch);
            currState.setStartNode(null);
            startNode_btn.setText("Use default");
            home_icon.setIcon(MaterialIcon.ARROW_BACK);
        }
        else {
            startSearch.clear();
            top_nav.getChildren().remove(startSearch);
            currState.setDefaultStartNode();
            event.setEventName("refresh");
            ApplicationState.getApplicationState().getFeb().updateEvent(event);
            event.setEventName("showSearch");    // Repopulate list
            ApplicationState.getApplicationState().getFeb().updateEvent(event);
            startNode_btn.setText("Start Node");
            home_icon.setIcon(MaterialIcon.LOCATION_ON);
        }
    }

    public void showEditEmployee(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) auth_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.employeeEdit);
        StageManager.changeExistingWindow(stage, root, "Edit Employees");
        stage.setFullScreen(true);

    }

    public void showPathSetting(MouseEvent mouseEvent) {
        event = ApplicationState.getApplicationState().getFeb().getEvent();
        if (barOpened){
            barOpened = false;
            event.setEventName("closeDrawer");
            ApplicationState.getApplicationState().getFeb().updateEvent(event);
        } else {
            barOpened = true;
            event.setEventName("showPathSetting");
            ApplicationState.getApplicationState().getFeb().updateEvent(event);
        }
    }
}
