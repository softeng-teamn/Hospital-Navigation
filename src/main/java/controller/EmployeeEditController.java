package controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.collections.ObservableSequentialListWrapper;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import model.Employee;
import model.JobType;
import service.DatabaseService;

import java.util.List;

public class EmployeeEditController {
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    @FXML
    private VBox root;

    @FXML
    private HBox hbox_container;

    @FXML
    private TableView<Employee> employee_table;

    @FXML
    private TableColumn<Employee, Integer> col_id;

    @FXML
    private TableColumn<Employee, String> col_username;

    @FXML
    private TableColumn<Employee, JobType> col_job;

    @FXML
    private TableColumn<Employee, String> col_admin;

    @FXML
    private JFXTextField new_username;

    @FXML
    private JFXComboBox<JobType> new_job;

    @FXML
    private JFXCheckBox new_is_admin;

    @FXML
    private JFXPasswordField new_password;

    @FXML
    private JFXPasswordField new_password_conf;

    @FXML
    public void initialize() {
        new_job.getItems().setAll(JobType.values());
        initCols();
        loadData();
    }

    private void initCols() {
       col_id.setCellValueFactory(new PropertyValueFactory<>("ID"));
       col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
       col_job.setCellValueFactory(new PropertyValueFactory<>("job"));
       col_admin.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

       editableCols();
    }

    private void editableCols() {
        col_username.setCellFactory(TextFieldTableCell.forTableColumn());
        col_username.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setUsername(e.getNewValue());
            myDBS.updateEmployee(employee);
            loadData();
        });

        col_job.setCellFactory(ComboBoxTableCell.forTableColumn(JobType.values()));
        col_job.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setJob(e.getNewValue());
            myDBS.updateEmployee(employee);
            loadData();
        });

        col_admin.setCellFactory(ComboBoxTableCell.forTableColumn("true", "false"));
        col_admin.setOnEditCommit(e -> {
            Employee employee = e.getTableView().getItems().get(e.getTablePosition().getRow());
            employee.setAdmin(Boolean.parseBoolean(e.getNewValue()));
            myDBS.updateEmployee(employee);
            loadData();
        });

        // switch to edit mode on MouseClick
        employee_table.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            TablePosition focusedCellPosition = employee_table.getFocusModel().getFocusedCell();
            employee_table.edit(focusedCellPosition.getRow(), focusedCellPosition.getTableColumn());
        });
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
        // TODO: verify password match
        // TODO: better ID generation

        int max = -1;
        for (Employee e : employee_table.getItems()) {
            max = e.getID() > max ? e.getID() : max;
        }

        Employee employee = new Employee(max+1, new_username.getText(), new_job.getValue(), new_is_admin.isSelected(), new_password.getText());
        myDBS.insertEmployee(employee);
        loadData();
    }
}
