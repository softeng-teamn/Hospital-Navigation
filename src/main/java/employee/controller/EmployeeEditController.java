package employee.controller;

import application_state.ApplicationState;
import com.jfoenix.controls.*;
import database.DatabaseService;
import employee.model.Employee;
import employee.model.JobType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.ResourceLoader;
import service.StageManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * controller for the employee editor FXML
 */
public class EmployeeEditController {
    static DatabaseService myDBS = DatabaseService.getDatabaseService();

    public static BufferedImage employeeImage = null;

    @FXML
    private Button homeBtn;

    @FXML
    private JFXButton img_btn;

    @FXML
    private TableView<Employee> employee_table;

    @FXML
    private TableColumn<Employee, Integer> col_id;

    @FXML
    private TableColumn<Employee, String> col_username;

    @FXML
    private TableColumn<Employee, JobType> col_job;

    @FXML
    private TableColumn<Employee, String> col_admin, col_email, col_phone;

    @FXML
    private JFXTextField new_username;

    @FXML
    private JFXComboBox<String> new_job;

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
        new_job.getItems().add(JobType.ADMINISTRATOR.toString());
        new_job.getItems().add(JobType.DOCTOR.toString());
        new_job.getItems().add(JobType.NURSE.toString());
        new_job.getItems().add(JobType.JANITOR.toString());
        new_job.getItems().add(JobType.SECURITY_PERSONNEL.toString());
        new_job.getItems().add(JobType.MAINTENANCE_WORKER.toString());
        new_job.getItems().add(JobType.IT.toString());
        new_job.getItems().add(JobType.GUEST.toString());
        new_job.getItems().add(JobType.RELIGIOUS_OFFICIAL.toString());
        new_job.getItems().add(JobType.GIFT_SERVICES.toString());
        new_job.getItems().add(JobType.MISCELLANEOUS.toString());
        new_job.getItems().add(JobType.AV.toString());
        new_job.getItems().add(JobType.INTERPRETER.toString());
        new_job.getItems().add(JobType.TOY.toString());
        new_job.getItems().add(JobType.PATIENT_INFO.toString());
        new_job.getItems().add(JobType.FLORIST.toString());
        new_job.getItems().add(JobType.INTERNAL_TRANSPORT.toString());
        new_job.getItems().add(JobType.EXTERNAL_TRANSPORT.toString());
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
    void setImage() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(ResourceLoader.takePhoto);
        stage.setScene(new Scene(root));
        stage.setTitle("Take Photo");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(homeBtn.getScene().getWindow());
        stage.showAndWait();

        System.out.println("Is employeeImage null?? -> " + employeeImage==null);

        if (employeeImage != null) {
            img_btn.setText("Re-take Image");
        }
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

        Employee employee = new Employee(max+1, new_username.getText(), JobType.valueOf(new_job.getValue()), new_is_admin.isSelected(), new_password.getText());
        boolean inserted = myDBS.insertEmployee(employee);
        loadData();

        if (inserted) {
            if (employeeImage != null) {
                // save the employeeImage
                // but I need the employeeID.... how do I get that
                Employee e = myDBS.getEmployeeByUsername(new_username.getText());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    ImageIO.write(employeeImage, "png", os);
                } catch (IOException err) {
                    err.printStackTrace();
                }
                InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                myDBS.updateEmployeeImage(e.getID(), inputStream);
            }
            new_password.setText("");
            new_password_conf.setText("");
            new_username.setText("");
            new_job.getSelectionModel().select(1);
            new_is_admin.setSelected(false);
            img_btn.setText("Take Image");

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
