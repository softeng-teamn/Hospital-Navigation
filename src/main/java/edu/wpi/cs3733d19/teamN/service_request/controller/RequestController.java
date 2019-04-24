package edu.wpi.cs3733d19.teamN.service_request.controller;

import edu.wpi.cs3733d19.teamN.application_state.ApplicationState;
//import bishopfishapi.Emergency;
import com.jfoenix.controls.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import foodRequest.FoodRequest;
import foodRequest.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import edu.wpi.cs3733d19.teamN.application_state.Event;
import edu.wpi.cs3733d19.teamN.map.Node;
import requests.giftrequests.RunGiftRequest;
import edu.wpi.cs3733d19.teamN.service_request.controller.sub_controller.InternalTransportController;
import edu.wpi.cs3733d19.teamN.service_request.model.Request;
import edu.wpi.cs3733d19.teamN.database.DatabaseService;
import edu.wpi.cs3733d19.teamN.service.ResourceLoader;
import edu.wpi.cs3733d19.teamN.service.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.wpi.cs3733d19.teamN.service.ResourceLoader.enBundle;
import static edu.wpi.cs3733d19.teamN.service.ResourceLoader.esBundle;

public class RequestController implements Initializable {

    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton englishBtn;
    @FXML
    private JFXButton spanishBtn;
    @FXML
    private JFXListView<HBox> list_view;
    @FXML
    private JFXTextField search_bar;
    @FXML
    private Pane subSceneHolder;

    private Collection<Request> requests;
    private Collection<Request> pendingRequests;

    private ArrayList<Node> allNodes;
    private ObservableList<Node> allNodesObservable;
    private HashMap<String, String> buildingAbbrev = new HashMap<>();    // Abbreviate buildings to fit in listview

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @SuppressFBWarnings(value="MS_CANNOT_BE_FINAL", justification = "I need to")
    public static Node selectedNode = null;

    private Event event;

    /**
     * initializes the service_request edu.wpi.cs3733d19.teamN.controller
     *
     * @param location feels needed to override
     * @param resources field needed to override
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        buildingAbbrev.put("Shapiro", "Sha");    // Set all building abbreviations
        buildingAbbrev.put("BTM", "BTM");
        buildingAbbrev.put("Tower", "Tow");
        buildingAbbrev.put("45 Francis", "45Fr");
        buildingAbbrev.put("15 Francis", "15Fr");
        buildingAbbrev.put("RES", "RES");
        buildingAbbrev.put("FLEX", "FLEX");

        repopulateList();
    }

    /**
     * switches window to home screen
     *
     * @throws Exception if the FXML fails to load
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
     * @throws Exception if the FXML fails to load
     */
    @FXML
    public void showEnglish() throws Exception{
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setCurrentBundle(enBundle);
        Stage stage = (Stage) englishBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request,event.getCurrentBundle());
        StageManager.changeExistingWindow(stage,root,"Service Request");
    }

    /**
     * switches to Spanish
     *
     * @throws Exception if the FXML fails to load
     */
    @FXML
    public void showSpanish() throws Exception{
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        event.setCurrentBundle(esBundle);
        Stage stage = (Stage) spanishBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.request,event.getCurrentBundle());
        StageManager.changeExistingWindow(stage,root,"Service Request");
    }


    /**
     * show every nodes on  JFXListView
     * @param e FXML event that calls this method
     */
    @FXML
    public void searchBarEnter(javafx.event.Event e) {
        String search = search_bar.getText();
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
            ObservableList<HBox> observeHboxes = makeIntoHBoxes(allNodes);
            list_view.getItems().addAll(observeHboxes);
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
            ObservableList<HBox> toShow = makeIntoHBoxes((ArrayList) filteredNodes);

            // Add to view
            list_view.setItems(toShow);
        }
    }

    /**
     * Populates the list of nodes based on the logged in user.
     * Admins have access to every node, while basic users can only see rooms.
     */
    void repopulateList() {

        System.out.println("Repopulation of listView");
        // if nobody is logged in, filter out stair and hall nodes
        if (ApplicationState.getApplicationState().getEmployeeLoggedIn() == null){
            allNodes = myDBS.getNodesFilteredByType("STAI", "HALL");
        }
        // if the user is admin, get everything
        else if (ApplicationState.getApplicationState().getEmployeeLoggedIn().isAdmin()) {
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

        ObservableList<HBox> observeHboxes = makeIntoHBoxes(allNodes);
        // add to listView
        list_view.setItems(observeHboxes);
    }

    /**
     * Make the passed in arraylist into an observable list of hboxes with name, building, floor
     * to put into the listview
     * @param nodes the list of nodes to display
     * @return the list of hboxes, one for each node
     */
    private ObservableList<HBox> makeIntoHBoxes(ArrayList<Node> nodes) {
        ArrayList<HBox> hBoxes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {    // For every node
            Node currNode = nodes.get(i);
            HBox hb = new HBox();
            HBox inner = new HBox();    // So the building can be right-aligned
            inner.setAlignment(Pos.CENTER_RIGHT);
            Label longName = new Label(currNode.getLongName());    // Make a label for the long name
            String buildFlStr = buildingAbbrev.get(currNode.getBuilding()) + ", " + currNode.getFloor();
            Label buildFloor = new Label(buildFlStr);    // Make a label for the building abbreviation and floor
            Label nodeId = new Label(currNode.getNodeID());    // Save the nodeID for pathfinding but make it invisible
            nodeId.setPrefWidth(0);
            nodeId.setVisible(false);
            nodeId.setPadding(new Insets(0, -10, 0, 0));
            hb.getChildren().add(longName);    // Add the node name
            inner.getChildren().add(nodeId);
            inner.getChildren().add(buildFloor);    // Add the ID and building and floor to the right-aligned hbox
            hb.getChildren().add(inner);    // Combine them
            hb.setHgrow(inner, Priority.ALWAYS);
            hb.setSpacing(0);
            hBoxes.add(hb);    // Add it all to the list
        }
        ObservableList<HBox> observeHboxes = FXCollections.observableArrayList();
        observeHboxes.addAll(hBoxes);
        return observeHboxes;
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
    public void giftSelect (ActionEvent actionEvent) throws Exception {
        RunGiftRequest rgr = new RunGiftRequest();
        rgr.run(0,0, 1920, 1080, null, null, null);
    }

    @FXML
    public void selectSanitation(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.sanitationRequest,event.getCurrentBundle()));
    }

    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "I need to")
    @FXML
    public void locationSelected(MouseEvent mouseEvent) {
        selectedNode = DatabaseService.getDatabaseService().getNode(((Label) ((HBox) list_view.getSelectionModel().getSelectedItem().getChildren().get(1)).getChildren().get(0)).getText());
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

//    public void emergencySelect(ActionEvent actionEvent) throws Exception {
//        Emergency emergency = new Emergency();
//        Emergency.setSender("neonnarwhalsd19");
//        Emergency.setSenderPassword("neonnarwhalsD19!");
//        Emergency.setRecipient(ApplicationState.getApplicationState().getEmployeeLoggedIn().getEmail());
//        emergency.run(50,50, 1500, 1000, null, "", "");
//    }
}