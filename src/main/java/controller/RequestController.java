package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Node;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class RequestController extends Controller implements Initializable {

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private ChoiceBox<String> locationBox;
    @FXML
    private ChoiceBox<String> typeBox;
    @FXML
    private JFXTextField locationTextField;
    @FXML
    private JFXTextField typeTextField;

    @FXML
    private TextArea textArea;

    private Collection<Request> requests;
    private Collection<Request> pendingRequests;


    @FXML
    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }


    @FXML
    //show every nodes on  JFXListView
    void showLocation(){

    }


    // submits request to database
    // "confirm" button
    @FXML
    public void makeRequest() {

        String descrption = (String) textArea.getText();
        String requestType = typeBox.getValue();
        String requestLocation = locationBox.getValue();

        if (requestLocation == null){
            locationTextField.setText("Request Location: \nPlease select location!");
        } else if(requestType == null){
            typeTextField.setText("Request Type: \nPlease select type!");
        } else if(requestType.contains("Medicine")){
            MedicineRequest newMedicineRequest = new MedicineRequest("123", descrption, null, false);
            textArea.setText("Medicine");
//            insertRequest(newMedicineRequest)
        } else if(requestType.contains("IT")){
            ITRequest newITRequest = new ITRequest("234", descrption, null, false);
            textArea.setText("IT");
//            insertRequest(newITRequest);
        }




    }

    // removes object from database
    void fufillRequest(String requestID, String byWho) {

    }

    // getter for pendingRequests
    public Collection<Request> getPendingRequests () {
        return null;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<Node> everyNode =  dbs.getAllNodes();
        int length = everyNode.size();
        ObservableList<String> locationList = FXCollections.observableArrayList("1","2","3");
        ObservableList<String> typeList = FXCollections.observableArrayList("Medicine Request", "IT Request");


//        for (int i = 0; i < 3; i++){
//
//            listview.add(everyNode.get(i).getLongName());
//
//
//        }

        locationBox.getItems().addAll(locationList);
        typeBox.getItems().addAll(typeList);

    }
}
