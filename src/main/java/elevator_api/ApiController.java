package elevator_api;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import employee.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ApiController implements Initializable {
    static ApiDatabaseService myDBS = ApiDatabaseService.getDatabaseService();

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
    private TableColumn<ApiInternalTransportRequest, String> col_open_urgency;

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
    private TableColumn<ApiInternalTransportRequest, String> col_assigned_urgency;

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

        col_assigned_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_assigned_location.setCellValueFactory(new PropertyValueFactory<>("location"));
        col_assigned_details.setCellValueFactory(new PropertyValueFactory<>("notes"));
        col_assigned_type.setCellValueFactory(new PropertyValueFactory<>("transport"));

        employeeComboBox.setCellFactory(param -> {
            final ListCell<Employee> cell = new ListCell<Employee>() {
                @Override public void updateItem(Employee item, boolean empty) {
                    if (item != null) {
                        setText(item.getUsername());
                    } else {
                        setText(null);
                    }
                }
            };
            return cell;
        });


        ObservableList<Employee> employees = FXCollections.observableArrayList();
        employees.setAll(myDBS.getAllEmployees());
        employeeComboBox.setItems(employees);
        employeeComboBox.getSelectionModel().select(0);
        loadData();
    }


    @FXML
    public void submitAction(javafx.event.ActionEvent actionEvent) {
        ApiInternalTransportRequest request = new ApiInternalTransportRequest(-1, text_area.getText(),  InternalTransportRequestApi.originNodeID, dropdown.getSelectionModel().getSelectedItem());
        myDBS.insertInternalTransportRequest(request);
        dropdown.getSelectionModel().select(0);
        text_area.setText("");


        dropdown.setItems(FXCollections.observableArrayList(ApiInternalTransportRequest.TransportType.values()));
        dropdown.getSelectionModel().select(0);

        loadData();
    }

    public void onAssignTo(ActionEvent actionEvent) {

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
