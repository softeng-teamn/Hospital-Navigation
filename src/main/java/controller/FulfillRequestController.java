package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import controller.requests.InternalTransportController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Employee;
import model.JobType;
import model.request.*;
import service.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static model.JobType.*;

public class FulfillRequestController extends Controller implements Initializable {

    @FXML
    private JFXButton homeBtn;
    @FXML
    private JFXButton adminBtn;
    @FXML
    private JFXListView<Request> requestListView;
    @FXML
    private JFXListView<Employee> employeeListView;
    @FXML
    private JFXComboBox<String> typeCombo;
    @FXML
    private JFXRadioButton allRadio;
    @FXML
    private JFXRadioButton uncRadio;
    @FXML
    private VBox typeVBox;

    ArrayList<Employee> employees;
    ArrayList<Request> requests;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    /**
     * switches window to home screen
     *
     * @throws Exception
     */
    @FXML
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

    @FXML
    void typeFilterSwitch(ActionEvent event) {
        String requestTypeSelected = typeCombo.getSelectionModel().getSelectedItem();

        employeeListView.getItems().clear();
        for (Employee employee : employees) {
            if (requestTypeSelected.equals("All") || employee.getJob() == ADMINISTRATOR) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("AV Service") && employee.getJob() == AV) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("External Transport") && employee.getJob() == EXTERNAL_TRANSPORT) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Florist") && employee.getJob() == FLORIST) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Gift Store") && employee.getJob() == GIFT_SERVICES) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Internal Transport") && employee.getJob() == INTERNAL_TRANSPORT) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Interpreter") && employee.getJob() == INTERPRETER) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("IT") && employee.getJob() == IT) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Maintenance") && employee.getJob() == MAINTENANCE_WORKER) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Medicine") && (employee.getJob() == DOCTOR || employee.getJob() == NURSE)) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Patient Info") && employee.getJob() == NURSE) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Religious") && employee.getJob() == RELIGIOUS_OFFICIAL) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Sanitation") && employee.getJob() == JANITOR) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Security") && employee.getJob() == SECURITY_PERSONNEL) {
                employeeListView.getItems().add(employee);
            } else if (requestTypeSelected.equals("Toy") && employee.getJob() == TOY) {
                employeeListView.getItems().add(employee);
            }
        }
    }


    /**
     * initialize the list of requests
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] requestTypes = {"All", "AV Service", "External Transport", "Florist", "Gift Store", "Internal Transport", "Interpreter", "IT", "Maintenance", "Patient Info", "Religious", "Sanitation", "Security", "Toy"};
        typeCombo.setItems(FXCollections.observableArrayList(requestTypes));
        typeCombo.getSelectionModel().select(0);

        employees = (ArrayList<Employee>) myDBS.getAllEmployees();

        employeeListView.setItems(FXCollections.observableArrayList(myDBS.getAllEmployees()));
        employeeListView.setCellFactory(new Callback<ListView<Employee>, ListCell<Employee>>() {
            @Override
            public ListCell<Employee> call(ListView<Employee> param) {
                ListCell<Employee> cell = new ListCell<Employee>() {

                    @Override
                    protected void updateItem(Employee item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getUsername() + " (" + item.getID() + ")");
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });

        requests = new ArrayList<>();
        requests.addAll(myDBS.getAllAVServiceRequests());
        requests.addAll(myDBS.getAllExtTransRequests());
        requests.addAll(myDBS.getAllFloristRequests());
        requests.addAll(myDBS.getAllGiftStoreRequests());
        requests.addAll(myDBS.getAllInternalTransportRequest());
        requests.addAll(myDBS.getAllInterpreterRequests());
        requests.addAll(myDBS.getAllITRequests());
        requests.addAll(myDBS.getAllMaintenanceRequests());
        requests.addAll(myDBS.getAllMedicineRequests());
        requests.addAll(myDBS.getAllPatientInfoRequests());
        requests.addAll(myDBS.getAllReligiousRequests());
        requests.addAll(myDBS.getAllSanitationRequests());
        requests.addAll(myDBS.getAllSecurityRequests());
        requests.addAll(myDBS.getAllToyRequests());

        System.out.println(myDBS.getAllITRequests());

        requestListView.setItems(FXCollections.observableArrayList(requests));
        requestListView.setCellFactory(new Callback<ListView<Request>, ListCell<Request>>() {
            @Override
            public ListCell<Request> call(ListView<Request> param) {
                ListCell<Request> cell = new ListCell<Request>() {

                    @Override
                    protected void updateItem(Request item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.toDisplayString());
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });
    }
}
