package controller;

import com.jfoenix.controls.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.Node;
import model.request.Request;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.io.IOException;
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

    @SuppressFBWarnings(value="MS_CANNOT_BE_FINAL", justification = "I need to")
    public static Node selectedNode = null;

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

    @FXML
    public void internalTransportSelect(ActionEvent e) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.internalTransportRequest));
    }


    @FXML
    public void patientSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.patientInfoRequest));
    }

    @FXML
    public void maintenanceRequest(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.maintenanceRequest));
    }

    @FXML
    public void floristSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.floristRequest));
    }

    @SuppressFBWarnings(value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "I need to")
    @FXML
    public void locationSelected(MouseEvent mouseEvent) {
        selectedNode = (Node) list_view.getSelectionModel().getSelectedItem();
    }

    public void toyRequestSelect(ActionEvent actionEvent) throws IOException {
        subSceneHolder.getChildren().clear();
        subSceneHolder.getChildren().add(FXMLLoader.load(ResourceLoader.ToyRequest));
    }
}