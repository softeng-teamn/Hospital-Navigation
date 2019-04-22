package employee.controller;

import application_state.ApplicationState;
import com.jfoenix.controls.*;
import employee.model.Employee;
import employee.model.JobType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import database.DatabaseService;
import service.ResourceLoader;
import service.StageManager;

import java.util.List;

/**
 * controller for the employee editor FXML
 */
public class EmployeeEditController {
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @FXML
    private Button homeBtn;

    @FXML
    private TableView<Employee> employee_table;

    @FXML
    private TableColumn<Employee, Integer> col_id;

    @FXML
    private TableColumn<Employee, String> col_username;

    @FXML
    private TableColumn<Employee, String> col_firstname;

    @FXML
    private TableColumn<Employee, String> col_lastname;

    @FXML
    private TableColumn<Employee, JobType> col_job;

    @FXML
    private TableColumn<Employee, String> col_admin, col_email, col_phone;

    @FXML
    private JFXTextField new_username, new_firstname, new_lastname;

    @FXML
    private JFXComboBox<JobType> new_job;

    @FXML
    private JFXCheckBox new_is_admin;

    @FXML
    private JFXPasswordField new_password;

    @FXML
    private JFXPasswordField new_password_conf;

    @FXML
    private JFXButton remove;

    @FXML
    public void initialize() {
        new_job.getItems().addAll(JobType.values());
        initCols();
        loadData();
    }

    private void initCols() {
       col_id.setCellValueFactory(new PropertyValueFactory<>("ID"));
       col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
       col_job.setCellValueFactory(new PropertyValueFactory<>("job"));
       col_admin.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));
       col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
       col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
       col_firstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
       col_lastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));

       editableCols();
    }

    private void editableCols() {
        employee_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            remove.setDisable(false);
        });

        col_username.setCellFactory(TextFieldTableCell.forTableColumn());
        col_username.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setUsername(e.getNewValue());
            myDBS.updateEmployee(employee);
            if(ApplicationState.getApplicationState().getEmployeeLoggedIn().getID() == employee.getID()) {
                ApplicationState.getApplicationState().setEmployeeLoggedIn(employee);
            }
            loadData();
        });

        col_firstname.setCellFactory(TextFieldTableCell.forTableColumn());
        col_firstname.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setFirstName(e.getNewValue());
            myDBS.updateEmployee(employee);
            if(ApplicationState.getApplicationState().getEmployeeLoggedIn().getID() == employee.getID()) {
                ApplicationState.getApplicationState().setEmployeeLoggedIn(employee);
            }
            loadData();
        });

        col_lastname.setCellFactory(TextFieldTableCell.forTableColumn());
        col_lastname.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setLastName(e.getNewValue());
            myDBS.updateEmployee(employee);
            if(ApplicationState.getApplicationState().getEmployeeLoggedIn().getID() == employee.getID()) {
                ApplicationState.getApplicationState().setEmployeeLoggedIn(employee);
            }
            loadData();
        });

        col_phone.setCellFactory(TextFieldTableCell.forTableColumn());
        col_phone.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setPhone(e.getNewValue());
            myDBS.updateEmployee(employee);
            if(ApplicationState.getApplicationState().getEmployeeLoggedIn().getID() == employee.getID()) {
                ApplicationState.getApplicationState().setEmployeeLoggedIn(employee);
            }
            loadData();
        });

        col_email.setCellFactory(TextFieldTableCell.forTableColumn());
        col_email.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setEmail(e.getNewValue());
            myDBS.updateEmployee(employee);
            if(ApplicationState.getApplicationState().getEmployeeLoggedIn().getID() == employee.getID()) {
                ApplicationState.getApplicationState().setEmployeeLoggedIn(employee);
            }
            loadData();
        });

        col_job.setCellFactory(ComboBoxTableCell.forTableColumn(JobType.values()));
        col_job.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setJob(e.getNewValue());
            myDBS.updateEmployee(employee);
            if(ApplicationState.getApplicationState().getEmployeeLoggedIn().getID() == employee.getID()) {
                ApplicationState.getApplicationState().setEmployeeLoggedIn(employee);
            }
            loadData();
        });

        col_admin.setCellFactory(ComboBoxTableCell.forTableColumn("true", "false"));
        col_admin.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setAdmin(Boolean.parseBoolean(e.getNewValue()));
            myDBS.updateEmployee(employee);
            if(ApplicationState.getApplicationState().getEmployeeLoggedIn().getID() == employee.getID()) {
                ApplicationState.getApplicationState().setEmployeeLoggedIn(employee);
            }
            loadData();
        });

        new_job.getSelectionModel().select(1);
    }

    private void loadData() {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        List<Employee> cleanEmployees = myDBS.getAllEmployees();
        employees.addAll(cleanEmployees);

        employee_table.setItems(employees);
        employee_table.setEditable(true);
    }


    @FXML
    void addNewEmployee(ActionEvent event) {
        if (!new_password.getText().equals(new_password_conf.getText())) {
            new_password.getStyleClass().add("wrong-credentials");
            new_password_conf.getStyleClass().add("wrong-credentials");
            return;
        } else {
            new_password.getStyleClass().remove("wrong-credentials");
            new_password_conf.getStyleClass().remove("wrong-credentials");
        }


        int max = -1;
        for (Employee e : employee_table.getItems()) {
            max = e.getID() > max ? e.getID() : max;
        }
        // todo: error check for empty fields
        Employee employee = new Employee(max+1, new_username.getText(), new_firstname.getText(), new_lastname.getText(),new_job.getValue(), new_is_admin.isSelected(), new_password.getText());
        boolean inserted = myDBS.insertEmployee(employee);
        loadData();

        if (inserted) {
            new_password.setText("");
            new_password_conf.setText("");
            new_username.setText("");
            new_firstname.setText("");
            new_lastname.setText("");
            new_job.getSelectionModel().select(1);
            new_is_admin.setSelected(false);

            new_username.getStyleClass().remove("wrong-credentials");
        } else {
            new_username.getStyleClass().add("wrong-credentials");
        }
    }


    @FXML
    void onRemoveEmployee(ActionEvent event) {
        Employee employee = employee_table.getSelectionModel().getSelectedItem();
        myDBS.deleteEmployee(employee);
        loadData();
        remove.setDisable(true);
    }

    public void showHome(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) homeBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(ResourceLoader.home);
        StageManager.changeExistingWindow(stage, root, "Home (Path Finder)");
    }
}
