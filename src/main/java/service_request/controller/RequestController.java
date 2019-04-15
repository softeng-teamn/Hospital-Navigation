package service_request.controller;

import application_state.ApplicationState;
import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import foodRequest.FoodRequest;
import foodRequest.ServiceException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import application_state.Event;
import application_state.EventBusFactory;
import map.Node;
import service_request.controller.sub_controller.InternalTransportController;
import service_request.model.Request;
import database.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static service.ResourceLoader.enBundle;
import static service.ResourceLoader.esBundle;

public class RequestController implements Initializable {

    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton englishBtn;
    @FXML
    private JFXButton spanishBtn;
    @FXML
    private JFXListView list_view;
    @FXML
    private JFXTextField search_bar;
    @FXML
    private Pane subSceneHolder;

    private Collection<Request> requests;
    private Collection<Request> pendingRequests;

    private ArrayList<Node> allNodes;
    private ObservableList<Node> allNodesObservable;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @SuppressFBWarnings(value="MS_CANNOT_BE_FINAL", justification = "I need to")
    public static Node selectedNode = null;

    private Event event = EventBusFactory.getEvent();
    private EventBus eventBus = EventBusFactory.getEventBus();

    /**
     * initializes the service_request controller
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repopulateList();
    }

    /**
     * switches window to home screen
     *
     * @throws Exception
     */
    @FXML
    public void showHome() throws Exception {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    /**
     * switches to English
     *
     * @throws Exception
     */
    @FXML
    public void showEnglish() throws Exception{
        event.setCurrentBundle(enBundle);
        Stage stage = (Stage) englishBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request,event.getCurrentBundle());
        StageManager.changeExistingWindow(stage,root,"Service Request");
    }

    /**
     * switches to Spanish
     *
     * @throws Exception
     */
    @FXML
    public void showSpanish() throws Exception{
        event.setCurrentBundle(esBundle);
        Stage stage = (Stage) spanishBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request,event.getCurrentBundle());
        StageManager.changeExistingWindow(stage,root,"Service Request");
    }


    /**
     * show every nodes on  JFXListView
     */
    @FXML
    public void searchBarEnter(ActionEvent e) {
        String search = search_bar.getText();
        System.out.println(search);
        filterList(search);
    }

    @FXML
    public void securitySelect(ActionEvent e) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.securityRequest,event.getCurrentBundle()));
    }

    /**
     *for lists
     */
    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    /**
     * Filters the ListView based on the string
     */
    private void filterList(String findStr) {
        if (findStr.equals("")) {
            list_view.getItems().clear();
            list_view.getItems().addAll(allNodesObservable);
        } else {
            //Get List of all nodes
            ObservableList<Node> original = allNodesObservable;

            //Get Sorted list of nodes based on search value
            List<ExtractedResult> filtered = FuzzySearch.extractSorted(findStr, convertList(original, Node::getLongName), 75);

            // Map to nodes based on index
            Stream<Node> stream = filtered.stream().map(er -> {
                return original.get(er.getIndex());
            });

            // Convert to list and then to observable list
            List<Node> filteredNodes = stream.collect(Collectors.toList());
            ObservableList<Node> toShow = FXCollections.observableList(filteredNodes);

            // Add to view
            list_view.getItems().clear();
            list_view.getItems().addAll(toShow);
        }
    }

    /**
     * Populates the list of nodes based on the logged in user.
     * Admins have access to every node, while basic users can only see rooms.
     */
    void repopulateList() {

        System.out.println("Repopulation of listView");
        // if nobody is logged in, filter out stair and hall nodes
        if (ApplicationState.getApplicationState().getCurrentEmployee() == null){
            allNodes = myDBS.getNodesFilteredByType("STAI", "HALL");
        }
        // if the user is admin, get everything
        else if (ApplicationState.getApplicationState().getCurrentEmployee().isAdmin()) {
            allNodes = myDBS.getAllNodes();
            // filter out stair and hall nodes otherwise
        } else {
            allNodes = myDBS.getNodesFilteredByType("STAI", "HALL");
        }

        // wipe old observable
        allNodesObservable = FXCollections.observableArrayList();
        // repopulate
        allNodesObservable.addAll(allNodes);

        Collections.sort(allNodesObservable, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.getLongName().compareTo(o2.getLongName());
            }
        });

        // clear listVIEW
        if (list_view == null) {
            System.out.println("LIST VIEW IS NULL");
            return;
        }

        list_view.getItems().clear();
        // add to listView
        list_view.getItems().addAll(allNodesObservable);

        list_view.setCellFactory(param -> new JFXListCell<Node>() {
            @Override
            protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNodeID() == null) {
                    setText(null);
                } else {
                    setText(item.getLongName());
                }
            }
        });
    }

    @FXML
    public void internalTransportSelect(ActionEvent e) throws IOException {
        subSceneHolder.getChildren().clear();
        FXMLLoader subscene = new FXMLLoader(ResourceLoader.internalTransportRequest,event.getCurrentBundle());
        subscene.setController(new InternalTransportController());
        subSceneHolder.getChildren().add(subscene.load());
    }

    @FXML
    public void interpreterRequestSelect(ActionEvent e) throws IOException{
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.interpreterRequest,event.getCurrentBundle()));
    }

    @FXML
    public void religiousRequestSelect(ActionEvent e) throws IOException{
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.religiousRequest,event.getCurrentBundle()));
    }

    @FXML
    public void patientSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.patientInfoRequest,event.getCurrentBundle()));
    }

    @FXML
    public void maintenanceRequest(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.maintenanceRequest,event.getCurrentBundle()));
    }

    @FXML
    public void floristSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.floristRequest,event.getCurrentBundle()));
    }

    @FXML
    public void giftSelect (ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.giftStoreRequest,event.getCurrentBundle()));
    }

    @FXML
    public void selectSanitation(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.sanitationRequest,event.getCurrentBundle()));
    }

    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "I need to")
    @FXML
    public void locationSelected(MouseEvent mouseEvent) {
        selectedNode = (Node) list_view.getSelectionModel().getSelectedItem();
    }

    public void toyRequestSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.ToyRequest,event.getCurrentBundle()));
    }
  
    public void avSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.avServiceRequest,event.getCurrentBundle()));
   }
  
    public void externalTransportationRequest(ActionEvent actionEvent) throws IOException{
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.externalTransportRequest,event.getCurrentBundle()));
    }
  
    public void medicineSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.medicineRequest,event.getCurrentBundle()));
    }

    public void itSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.itRequest,event.getCurrentBundle()));
    }

    public void foodSelect(ActionEvent actionEvent) throws ServiceException {
        FoodRequest req = new FoodRequest();
        req.run(0, 0, 1920, 1080, null, null, null);
    }
}