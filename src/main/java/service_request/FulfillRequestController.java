package service_request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import employee.model.Employee;
import database.DatabaseService;
import javafx.util.StringConverter;
import service.ResourceLoader;
import service.StageManager;
import service_request.model.Request;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * controller for the request fulfilling FXML
 */
public class FulfillRequestController implements Initializable {

    @FXML
    private JFXButton homeBtn;

    @FXML
    private JFXComboBox<String> typeCombo;

    @FXML
    private JFXComboBox<Employee> employeeCombo;

    @FXML
    private ToggleGroup filterGroup;

    @FXML
    private TableView<Request> requestTable;

    @FXML
    private TableColumn<Request, String> colID, colType, colLocation, colDescription, colEmployee, colFufilled;

    private ObservableList<Request> requests;

    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    /**
     * initialize the list of requests
     *
     * @param location required parameter for the abstract method this overrides
     * @param resources required parameter for the abstract method this overrides
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] requestTypes = {"All", "AV Service", "External Transport", "Florist", "Gift Store", "Internal Transport", "Interpreter", "IT", "Maintenance", "Patient Info", "Religious", "Sanitation", "Security", "Toy", "Help"};
        typeCombo.setItems(FXCollections.observableArrayList(requestTypes));
        typeCombo.getSelectionModel().select(0);

        Callback<ListView<Employee>, ListCell<Employee>> factory = lv -> new ListCell<Employee>() {

            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getUsername());
            }

        };

        employeeCombo.setCellFactory(factory);
        employeeCombo.setButtonCell(factory.call(null));
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        employees.setAll(myDBS.getAllEmployees());
        employeeCombo.setItems(employees);
        employeeCombo.getSelectionModel().select(0);

        //selected value showed in combo box
        employeeCombo.setConverter(new StringConverter<Employee>() {
            @Override
            public String toString(Employee employee) {
                if (employee == null){
                    return null;
                } else {
                    return employee.getUsername();
                }
            }

            @Override
            public Employee fromString(String userId) {
                return null;
            }
        });

        requests = FXCollections.observableArrayList();
        requestTable.setItems(requests);
        setRequestsAll();
        initCols();
    }

    private void initCols() {
        colType.setCellValueFactory(param -> new SimpleStringProperty("" + param.getValue().getType()));
        colID.setCellValueFactory(param -> new SimpleStringProperty("" + param.getValue().getId()));
        colLocation.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLocation().getLongName()));
        colDescription.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().toDisplayString()));
        colEmployee.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAssignedTo() > 0 ? myDBS.getEmployee(param.getValue().getAssignedTo()).getUsername() : ""));
        colFufilled.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().isCompleted() ? "Yes" : "No"));
    }

    @FXML
    void typeFilterSwitch(ActionEvent event) {
        String requestTypeSelected = typeCombo.getSelectionModel().getSelectedItem();

        /*
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
            } else if (requestTypeSelected.equals("Help")){
                employeeListView.getItems().add(employee);
            }
        }

        requestListView.getItems().clear();
        for (Request request : requests) {
            if (requestTypeSelected.equals("All") || request.isOfType(requestTypeSelected)) {
                requestListView.getItems().add(request);
            }
        }
        */
    }

    private void setRequestsAll() {
        requests.clear();
        requests.addAll(myDBS.getAllAVServiceRequests());
        requests.addAll(myDBS.getAllExtTransRequests());
        requests.addAll(myDBS.getAllFloristRequests());
        requests.addAll(myDBS.getAllGiftStoreRequests());
        requests.addAll(myDBS.getAllInternalTransportRequests());
        requests.addAll(myDBS.getAllInterpreterRequests());
        requests.addAll(myDBS.getAllITRequests());
        requests.addAll(myDBS.getAllMaintenanceRequests());
        requests.addAll(myDBS.getAllMedicineRequests());
        requests.addAll(myDBS.getAllPatientInfoRequests());
        requests.addAll(myDBS.getAllReligiousRequests());
        requests.addAll(myDBS.getAllSanitationRequests());
        requests.addAll(myDBS.getAllSecurityRequests());
        requests.addAll(myDBS.getAllToyRequests());
        requests.addAll(myDBS.getAllHelpRequests());
    }

    private void setRequestsIncomplete() {
        requests.clear();
        requests.addAll(myDBS.getAllIncompleteAVServiceRequests());
        requests.addAll(myDBS.getAllIncompleteExtTransRequests());
        requests.addAll(myDBS.getAllIncompleteFloristRequests());
        requests.addAll(myDBS.getAllIncompleteGiftStoreRequests());
        requests.addAll(myDBS.getAllIncompleteInternalTransportRequests());
        requests.addAll(myDBS.getAllIncompleteInterpreterRequests());
        requests.addAll(myDBS.getAllIncompleteITRequests());
        requests.addAll(myDBS.getAllIncompleteMaintenanceRequests());
        requests.addAll(myDBS.getAllIncompleteMedicineRequests());
        requests.addAll(myDBS.getAllIncompletePatientInfoRequests());
        requests.addAll(myDBS.getAllIncompleteReligiousRequests());
        requests.addAll(myDBS.getAllIncompleteSanitationRequests());
        requests.addAll(myDBS.getAllIncompleteSecurityRequests());
        requests.addAll(myDBS.getAllIncompleteToyRequests());
        requests.addAll(myDBS.getAllIncompleteHelpRequests());
    }

    private void setRequestsComplete() {
        requests.clear();
        requests.addAll(myDBS.getAllCompleteAVServiceRequests());
        requests.addAll(myDBS.getAllCompleteExtTransRequests());
        requests.addAll(myDBS.getAllCompleteFloristRequests());
        requests.addAll(myDBS.getAllCompleteGiftStoreRequests());
        requests.addAll(myDBS.getAllCompleteInternalTransportRequests());
        requests.addAll(myDBS.getAllCompleteInterpreterRequests());
        requests.addAll(myDBS.getAllCompleteITRequests());
        requests.addAll(myDBS.getAllCompleteMaintenanceRequests());
        requests.addAll(myDBS.getAllCompleteMedicineRequests());
        requests.addAll(myDBS.getAllCompletePatientInfoRequests());
        requests.addAll(myDBS.getAllCompleteReligiousRequests());
        requests.addAll(myDBS.getAllCompleteSanitationRequests());
        requests.addAll(myDBS.getAllCompleteSecurityRequests());
        requests.addAll(myDBS.getAllCompleteToyRequests());
        requests.addAll(myDBS.getAllCompleteHelpRequests());
    }

    public void reqStateChange(ActionEvent actionEvent) {
        /*
        if (allRadio.isSelected()) {
            setRequestsAll();
        } else if(uncRadio.isSelected()) {
            setRequestsIncomplete();
        } else if(completedRadio.isSelected()) {
            setRequestsComplete();
        }

        requestListView.getItems().clear();
        String requestTypeSelected = typeCombo.getSelectionModel().getSelectedItem();
        for (Request request : requests) {
            if (requestTypeSelected.equals("All") || request.isOfType(requestTypeSelected)) {
                requestListView.getItems().add(request);
            }
        }*/
    }

    @FXML
    public void fulfillRequest(ActionEvent actionEvent) {
/*        Request selectedRequest = requestListView.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {
            if (selectedRequest.getAssignedTo() == 0) {
                selectedRequest.setAssignedTo(-1);
            }
            selectedRequest.fillRequest();
            reqStateChange(null);
        }*/
    }

    @FXML
    public void assignRequest(ActionEvent actionEvent) {
/*        Request selectedRequest = requestListView.getSelectionModel().getSelectedItem();
        Employee selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null && selectedRequest != null) {
            selectedRequest.setAssignedTo(selectedEmployee.getID());
            selectedRequest.updateEmployee(selectedRequest, selectedEmployee);
            reqStateChange(null);
        }*/
    }

    /**
     * switches window to home screen
     *
     * @throws Exception if the FXML fails to load
     */
    @FXML
    public void showHome() throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }

}
