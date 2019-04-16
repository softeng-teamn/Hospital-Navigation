package elevator_api;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleNode;
import elevator.ElevatorConnection;
import employee.model.Employee;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ApiController implements Initializable {
    static ApiDatabaseService myDBS = ApiDatabaseService.getDatabaseService();

    @FXML
    public JFXToggleNode low;

    @FXML
    private ToggleGroup urgency;

    @FXML
    private JFXComboBox<ApiInternalTransportRequest.TransportType> dropdown;
    @FXML
    private JFXTextArea text_area;

    @FXML
    private TableView<ApiInternalTransportRequest> open_table;

    @FXML
    private TableColumn<ApiInternalTransportRequest, Integer> col_open_id;

    @FXML
    private TableColumn<ApiInternalTransportRequest, String> col_open_location;

    @FXML
    private TableColumn<ApiInternalTransportRequest, ApiInternalTransportRequest.TransportType> col_open_type;

    @FXML
    private TableColumn<ApiInternalTransportRequest, ApiInternalTransportRequest.Urgency> col_open_urgency;

    @FXML
    private TableColumn<ApiInternalTransportRequest, String> col_open_details;

    @FXML
    private JFXButton assignTo;

    @FXML
    private JFXComboBox<Employee> employeeComboBox;

    @FXML
    private TableView<ApiInternalTransportRequest> assigned_table;

    @FXML
    private TableColumn<ApiInternalTransportRequest, Integer> col_assigned_id;

    @FXML
    private TableColumn<ApiInternalTransportRequest, String> col_assigned_location;

    @FXML
    private TableColumn<ApiInternalTransportRequest, ApiInternalTransportRequest.TransportType> col_assigned_type;

    @FXML
    private TableColumn<ApiInternalTransportRequest, ApiInternalTransportRequest.Urgency> col_assigned_urgency;

    @FXML
    private TableColumn<ApiInternalTransportRequest, String> col_assigned_to;

    @FXML
    private TableColumn<ApiInternalTransportRequest, String> col_assigned_details;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropdown.setItems(FXCollections.observableArrayList(ApiInternalTransportRequest.TransportType.values()));
        dropdown.getSelectionModel().select(0);
        text_area.setText("");
        dropdown.getSelectionModel().select(0);

        col_open_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_open_location.setCellValueFactory(new PropertyValueFactory<>("location"));
        col_open_details.setCellValueFactory(new PropertyValueFactory<>("notes"));
        col_open_type.setCellValueFactory(new PropertyValueFactory<>("transport"));
        col_open_urgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));

        col_assigned_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_assigned_location.setCellValueFactory(new PropertyValueFactory<>("location"));
        col_assigned_details.setCellValueFactory(new PropertyValueFactory<>("notes"));
        col_assigned_type.setCellValueFactory(new PropertyValueFactory<>("transport"));
        col_assigned_urgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        col_assigned_to.setCellValueFactory(p -> {
            if (p.getValue() != null) {
                return new SimpleStringProperty(myDBS.getEmployee(p.getValue().getAssignedTo()).getUsername());
            } else {
                return new SimpleStringProperty("error");
            }
        });

        Callback<ListView<Employee>, ListCell<Employee>> factory = lv -> new ListCell<Employee>() {

            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getUsername());
            }

        };

        employeeComboBox.setCellFactory(factory);
        employeeComboBox.setButtonCell(factory.call(null));


        ObservableList<Employee> employees = FXCollections.observableArrayList();
        employees.setAll(myDBS.getAllEmployees());
        employeeComboBox.setItems(employees);
        employeeComboBox.getSelectionModel().select(0);
        urgency.selectToggle(low);

        loadData();
    }


    @FXML
    public void submitAction(javafx.event.ActionEvent actionEvent) {
        ApiInternalTransportRequest.Urgency urgencyLevel = ApiInternalTransportRequest.Urgency.NOT;

        JFXToggleNode selected = (JFXToggleNode) urgency.getSelectedToggle();

        if (selected != null) {
            switch (selected.getText()) {
                case "Low":
                    urgencyLevel = ApiInternalTransportRequest.Urgency.NOT;
                    break;
                case "Medium":
                    urgencyLevel = ApiInternalTransportRequest.Urgency.SOMEWHAT;
                    break;
                case "High":
                    urgencyLevel = ApiInternalTransportRequest.Urgency.VERY;
                    break;
                default:
                    urgencyLevel = ApiInternalTransportRequest.Urgency.NOT;
            }
        }

        ApiInternalTransportRequest request = new ApiInternalTransportRequest(-1, text_area.getText(),  InternalTransportRequestApi.originNodeID, dropdown.getSelectionModel().getSelectedItem(), urgencyLevel);
        myDBS.insertInternalTransportRequest(request);
        dropdown.getSelectionModel().select(0);
        text_area.setText("");


        dropdown.setItems(FXCollections.observableArrayList(ApiInternalTransportRequest.TransportType.values()));
        dropdown.getSelectionModel().select(0);

        loadData();
    }

    //also calls elev
    public void onAssignTo(ActionEvent actionEvent) {
        ApiInternalTransportRequest selectedReq = open_table.getSelectionModel().getSelectedItem();
        Employee employee = employeeComboBox.getSelectionModel().getSelectedItem();

        if (selectedReq != null && employee != null) {
            selectedReq.setAssignedTo(employee.getID());
            myDBS.updateInternalTransportRequest(selectedReq);

            loadData();

            if(myDBS.isCallElev() && selectedReq.getUrgency() == ApiInternalTransportRequest.Urgency.VERY
                    && selectedReq.getLocation().length() >= 2){
                ElevatorConnection e = new ElevatorConnection();
                try {
                    System.out.println("Calling Elev");
                    e.postFloor(myDBS.getTeam() + "L", selectedReq.getLocation().substring(selectedReq.getLocation().length() - 2));
                } catch (IOException e1) {
                    System.out.println("error posting in onAssignedTo in DBS");
                    e1.printStackTrace();
                }
            }
        }
    }

    private void loadData() {
        ObservableList<ApiInternalTransportRequest> open_requests = FXCollections.observableArrayList();
        ObservableList<ApiInternalTransportRequest> assigned_requests = FXCollections.observableArrayList();

        List<ApiInternalTransportRequest> all_requests = myDBS.getAllInternalTransportRequests();

        for (ApiInternalTransportRequest req : all_requests){
            if(req.getAssignedTo() == -1 || req.getAssignedTo() == 0) {
                open_requests.add(req);
            } else {
                assigned_requests.add(req);
            }
        }

        open_table.setItems(open_requests);
        assigned_table.setItems(assigned_requests);
    }
}
