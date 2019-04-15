package service_request;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import employee.model.Employee;
import database.DatabaseService;
import service.ResourceLoader;
import service.StageManager;
import service_request.model.Request;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static employee.model.JobType.*;

public class FulfillRequestController implements Initializable {

    @FXML
    public JFXRadioButton completedRadio;
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

        requestListView.getItems().clear();
        for (Request request : requests) {
            if (requestTypeSelected.equals("All") || request.isOfType(requestTypeSelected)) {
                requestListView.getItems().add(request);
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

        setRequestsAll();

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

        requestListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Request> observable, Request oldValue, Request newValue) -> {
            employeeListView.getItems().clear();
            for(Employee employee : employees) {
                if (newValue == null || newValue.fulfillableByType(employee.getJob())) {
                    employeeListView.getItems().add(employee);
                }
            }
        });
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
    }

    public void reqStateChange(ActionEvent actionEvent) {
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
        }
    }

    public void fulfillRequest(ActionEvent actionEvent) {
        Request selectedRequest = requestListView.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {
            if (selectedRequest.getAssignedTo() == 0) {
                selectedRequest.setAssignedTo(-1);
            }
            selectedRequest.fillRequest();
            reqStateChange(null);
        }
    }

    public void assignRequest(ActionEvent actionEvent) {
        Request selectedRequest = requestListView.getSelectionModel().getSelectedItem();
        Employee selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null && selectedRequest != null) {
            selectedRequest.setAssignedTo(selectedEmployee.getID());
            selectedRequest.updateEmployee(selectedRequest, selectedEmployee);
            reqStateChange(null);
        }
    }
}
