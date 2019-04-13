package home;

import application_state.ApplicationState;
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
import application_state.Event;
import application_state.EventBusFactory;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class TopNavController {

    public HBox top_nav;
    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private JFXButton navigate_btn, fulfillBtn, auth_btn, bookBtn, startNode_btn, requestBtn;    // TODO: rename fulfillbtn and change icon
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


    @FXML
    void showAdminLogin(ActionEvent e) throws Exception {
        // when admin logs out
        if (event.isAdmin()) {
            event.setAdmin(false);
            event.setLoggedIn(false);
            resetBtn();
            ApplicationState.getApplicationState().setEmployeeLoggedIn(null);
            System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());
            // when employee logs out, or login process begins
        } else {
            Parent root = FXMLLoader.load(ResourceLoader.adminLogin);
            Stage stage = (Stage) navigate_btn.getScene().getWindow();
            StageManager.changeExistingWindow(stage, root, "Admin Login");
            ApplicationState.getApplicationState().setEmployeeLoggedIn(null);
            System.out.println("ApplicationState.getApplicationState().setEmployeeLoggedIn(null)" + ApplicationState.getApplicationState().getEmployeeLoggedIn());

        }
    }

    @FXML
    // switches window to map editor screen.
    public void showAdminScene() throws Exception {
        event.setEventName("showAdmin");
        eventBus.post(event);
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
        eventBus.register(this);

        // Turn off editing
        event.setEventName("editing");
        event.setEditing(false);
        eventBus.post(event);

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
                // remove if way off base
            case "empLogin":
                event.setLoggedIn((newEvent.isLoggedIn()));
                break ;
            case "showSearch":
                backArro.setRate(-1);
                backArro.play();
                barOpened = false;
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
            System.out.println("USER IS AN ADMIN");
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


        // show node-selected in search
        String fillNodeinSearch = selected.getLongName();

        if(event.isEndNode()){
            search_bar.setText(fillNodeinSearch);
        } else {
            startSearch.setText(fillNodeinSearch);
        }
    }

    public void startNavigation(ActionEvent actionEvent) {
        //if(callElev.isSelected()){
            event.setCallElev(true);
        //}

        event.setEventName("navigation");
        eventBus.post(event);
    }


    public void setEventEndNode(MouseEvent mouseEvent){

        event.setEndNode(false);
        event.setEventName("showSearch");
        eventBus.post(event);

        if(backArro.getRate() == 1) {
            backArro.setRate(backArro.getRate() * -1);
            backArro.play();
            barOpened = false;
        }
    }


    public void setEventStartNode(MouseEvent mouseEvent) {

        event.setEndNode(true);
        event.setEventName("showSearch");
        eventBus.post(event);

        if(backArro.getRate() == 1) {
            backArro.setRate(backArro.getRate() * -1);
            backArro.play();
            barOpened = false;
        }
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



    public void showEditEmployee(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) auth_btn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.employeeEdit);
        StageManager.changeExistingWindow(stage, root, "Edit Employees");
        stage.setFullScreen(true);

    }

    public void showPathSetting(MouseEvent mouseEvent) {
        if (barOpened){
            barOpened = false;
            event.setEventName("showSearch");
            eventBus.post(event);
        } else {
            barOpened = true;
            event.setEventName("showPathSetting");
            eventBus.post(event);
        }
    }
}
