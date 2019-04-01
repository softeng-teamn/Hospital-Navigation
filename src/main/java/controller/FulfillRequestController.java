package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import model.request.ITRequest;
import model.request.MedicineRequest;
import model.request.Request;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FulfillRequestController extends Controller implements Initializable {

    @FXML
    private JFXButton homeBtn;
    @FXML
    private JFXListView requestListView;

    @FXML
    // switches window to home screen
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.fulfillrequest);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }


    
    
    
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Request> requestlist = FXCollections.observableArrayList();

        ArrayList<MedicineRequest> medicineReq = (ArrayList<MedicineRequest>) dbs.getAllMedicineRequests();
        ArrayList<ITRequest> itReq = (ArrayList<ITRequest>) dbs.getAllITRequests();

        requestlist.addAll(medicineReq);
        requestlist.addAll(itReq);

        requestListView.setItems(requestlist);

        // Set the cell to display only the name of the reservableSpace
        requestListView.setCellFactory(param -> new ListCell<Request>() {
            @Override
            protected void updateItem(Request item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.toString() == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        requestListView.setEditable(false);

    }
}
