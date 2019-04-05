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
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.Node;
import model.RequestType;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
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

    private Collection<Request> requests;
    private Collection<Request> pendingRequests;

    ArrayList<Node> allNodes;
    ObservableList<Node> allNodesObservable;

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
     * submits request to database
     * "confirm" button
     */
    @FXML
    public void makeRequest() {
        JFXToggleNode selected = (JFXToggleNode) requestType.getSelectedToggle();

        String description = textArea.getText();
        Node requestLocation = (Node) list_view.getSelectionModel().getSelectedItem();

        if (requestLocation == null) {
            textArea.setText("Please select location");
        } else if (selected == null) {
            textArea.setText("Please select type");
        } else if (selected.getText().contains("Medicine")) {
            MedicineRequest newMedicineRequest = new MedicineRequest(-1, description, requestLocation, false);
            DatabaseService.getDatabaseService().insertMedicineRequest(newMedicineRequest);
        } else if (selected.getText().contains("IT")) {
            ITRequest newITRequest = new ITRequest(-1, description, requestLocation, false);
            DatabaseService.getDatabaseService().insertITRequest(newITRequest);

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
                if (DatabaseService.getDatabaseService().getITRequest(ITType.getId()) == null) {
                    DatabaseService.getDatabaseService().insertITRequest(ITType);
                }
                break;
            case MED:
                MedicineRequest medReq = (MedicineRequest) type;
                if (DatabaseService.getDatabaseService().getMedicineRequest(medReq.getId()) == null) {
                    DatabaseService.getDatabaseService().insertMedicineRequest(medReq);
                }
                break;
            case ABS:
                //dont make a request if its not a real type
        }
    }

    /**
     * removes object from database
     *
     * @param type
     * @param byWho
     */
    void fufillRequest(Request type, String byWho) {
        RequestType rType = type.getRequestType();
        switch (rType.getrType()) {
            case ITS:
                ITRequest ITReq = (ITRequest) type;
                ITReq.setCompleted(true);
                ITReq.setCompletedBy(byWho);
                DatabaseService.getDatabaseService().updateITRequest(ITReq);
                break;
            case MED:
                MedicineRequest MedReq = (MedicineRequest) type;
                MedReq.setCompleted(true);
                MedReq.setCompletedBy(byWho);
                DatabaseService.getDatabaseService().updateMedicineRequest(MedReq);
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
        requests.addAll(DatabaseService.getDatabaseService().getAllIncompleteITRequests());
        requests.addAll(DatabaseService.getDatabaseService().getAllIncompleteMedicineRequests());
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
            allNodes = DatabaseService.getDatabaseService().getAllNodes();
        } else {
            allNodes = DatabaseService.getDatabaseService().getNodesFilteredByType("STAI", "HALL");
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