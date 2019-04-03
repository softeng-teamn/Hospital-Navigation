package controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Node;
import model.RequestType;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.ResourceBundle;

public class RequestController extends Controller implements Initializable {

    @FXML
    private JFXButton cancelBtn;


    @FXML
    private JFXListView locationNodeList;

    @FXML
    private JFXTextArea textArea;
    @FXML
    private ToggleGroup requestType;

    private Collection<Request> requests;
    private Collection<Request> pendingRequests;


    /**
     * switches window to home screen
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
    void showLocation(){

    }


    /**
     * submits request to database
     * "confirm" button
     */
    @FXML
    public void makeRequest() {
        JFXToggleNode selected = (JFXToggleNode) requestType.getSelectedToggle();

        String description = textArea.getText();
        Node requestLocation = (Node) locationNodeList.getSelectionModel().getSelectedItem();

        if (requestLocation == null) {
            textArea.setText("Please select location");
        } else if (selected == null) {
            textArea.setText("Please select type");
        } else if (selected.getText().contains("Medicine")) {
            MedicineRequest newMedicineRequest = new MedicineRequest( -1, description, requestLocation, false);
            dbs.insertMedicineRequest(newMedicineRequest);
        } else if (selected.getText().contains("IT")) {
            ITRequest newITRequest = new ITRequest(-1, description, requestLocation, false);
            dbs.insertITRequest(newITRequest);

        }
        textArea.clear();
    }


    /**
     * Generates a request of the given type
     * @param type
     */
    void makeRequest(Request type) {
        RequestType rType = type.getRequestType();
        switch(rType.getrType()){
            case ITS:
                ITRequest ITType = (ITRequest) type;
                if(dbs.getITRequest(ITType.getId())==null) {
                    dbs.insertITRequest(ITType);
                }
                break;
            case MED:
                MedicineRequest medReq = (MedicineRequest) type;
                if(dbs.getMedicineRequest(medReq.getId())==null) {
                    dbs.insertMedicineRequest(medReq);
                }
                break;
            case ABS:
                //dont make a request if its not a real type
        }
    }

    /**
     * removes object from database
     * @param type
     * @param byWho
     */
    void fufillRequest(Request type, String byWho) {
        RequestType rType = type.getRequestType();
        switch(rType.getrType()){
            case ITS:
                ITRequest ITReq = (ITRequest) type;
                ITReq.setCompleted(true);
                ITReq.setCompletedBy(byWho);
                dbs.updateITRequest(ITReq);
                break;
            case MED:
                MedicineRequest MedReq = (MedicineRequest) type;
                MedReq.setCompleted(true);
                MedReq.setCompletedBy(byWho);
                dbs.updateMedicineRequest(MedReq);
                break;
            case ABS:
                //do nothing
        }
    }

    /**
     * getter for pendingRequests
     * @return
     */
    public Collection<Request> getPendingRequests () {
        ArrayList<Request> requests = new ArrayList<>();
        requests.addAll(this.dbs.getAllIncompleteITRequests());
        requests.addAll(this.dbs.getAllIncompleteMedicineRequests());
        return requests;
    }


    /**
     * initializes the request controller
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<Node> everyNode =  dbs.getNodesFilteredByType("STAI", "HALL");
        System.out.println(everyNode.size());

        ObservableList<Node> nodeList = FXCollections.observableArrayList(dbs.getAllNodes());

        locationNodeList.getItems().clear();
        locationNodeList.setItems(nodeList);

        // Set the cell to display only the name of the reservableSpace
        locationNodeList.setCellFactory(param -> new ListCell<Node>() {
            @Override
            protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getLongName() == null) {
                    setText(null);
                } else {
                    setText(item.getLongName());
                }
            }
        });
        locationNodeList.setEditable(false);

//        ObservableList<String> locationList = FXCollections.observableArrayList("1","2","3");
//        ObservableList<String> typeList = FXCollections.observableArrayList("Medicine Request", "IT Request");
//        locationNodeList.getItems().addAll(locationList);
//        typeBox.getItems().addAll(typeList);

    }


}
