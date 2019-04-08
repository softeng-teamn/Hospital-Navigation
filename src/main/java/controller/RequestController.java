package controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.Node;
import model.RequestType;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
import model.request.RequestFacade;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestController extends Controller implements Initializable {

    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXListView list_view;
    @FXML
    private JFXTextArea textArea;
    @FXML
    private ToggleGroup requestType;
    @FXML
    private JFXTextField search_bar;
    @FXML
    private Pane subSceneHolder;

    private Collection<Request> requests;
    private Collection<Request> pendingRequests;

    private ArrayList<Node> allNodes;
    private ObservableList<Node> allNodesObservable;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();
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
     * show every nodes on  JFXListView
     */
    @FXML
    public void searchBarEnter(ActionEvent e) {
        String search = search_bar.getText();
        System.out.println(search);
        filterList(search);
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
     * collects user input information, calls Facade class to create object and submit to database
     * "confirm" button
     */
    @FXML
    public void makeRequest() {

        JFXToggleNode selected = (JFXToggleNode) requestType.getSelectedToggle();
        String description = textArea.getText();
        Node requestLocation = (Node) list_view.getSelectionModel().getSelectedItem();

        // make sure fields are filled in
        if (requestLocation == null) {
            textArea.setText("Please select location");
        } else if (selected == null) {
            textArea.setText("Please select type");
        }

        // new Facade object
        RequestFacade reqFacade = new RequestFacade(selected,description, requestLocation) ;



        // if feilds are populted and are of type:
        if ((selected != null) && (selected.getText().contains("Medicine"))) {
            reqFacade.makeMedRequest();
        } else if ((selected != null) && (selected.getText().contains("IT"))) {
            reqFacade.makeITRequest();
        }
        textArea.clear();
    }


    /**
     * Generates a request of the given type
     *
     * @param type
     */
    void makeRequest(Request type) {
        RequestType rType = type.getRequestType();
        switch (rType.getrType()) {
            case ITS:
                ITRequest ITType = (ITRequest) type;
                if (myDBS.getITRequest(ITType.getId()) == null) {
                    myDBS.insertITRequest(ITType);
                }
                break;
            case MED:
                MedicineRequest medReq = (MedicineRequest) type;
                if (myDBS.getMedicineRequest(medReq.getId()) == null) {
                    myDBS.insertMedicineRequest(medReq);
                }
                break;
            case ABS:
                //dont make a request if its not a real type
        }
    }

    /**
     * removes object from database through Facade class
     *
     * @param type
     * @param byWho
     */
    void fufillRequest(Request type, String byWho) {
        RequestFacade reqFacade = new RequestFacade(type,byWho);
        RequestType rType = type.getRequestType();
        switch (rType.getrType()) {
            case ITS:
                reqFacade.fillITRequest();
                break;
            case MED:
                reqFacade.fillMedRequest();
                break;
            case ABS:
                //do nothing
        }
    }

    /**
     * getter for pendingRequests
     *
     * @return
     */
    public Collection<Request> getPendingRequests() {
        ArrayList<Request> requests = new ArrayList<>();
        requests.addAll(myDBS.getAllIncompleteITRequests());
        requests.addAll(myDBS.getAllIncompleteMedicineRequests());
        return requests;
    }


    /**
     * initializes the request controller
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repopulateList();
    }

    /**
     * populates list based on the user
     */
    void repopulateList() {
        System.out.println("Repopulation of listView");
        if (Controller.getIsAdmin()) {
            allNodes = myDBS.getAllNodes();
        } else {
            allNodes = myDBS.getNodesFilteredByType("STAI", "HALL");
        }
        // wipe old observable
        allNodesObservable = FXCollections.observableArrayList();
        // repopulate
        allNodesObservable.addAll(allNodes);
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
}