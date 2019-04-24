package edu.wpi.cs3733d19.teamN.service;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * FXML loader
 */
public class  ResourceLoader {

    public static final URL home = ResourceLoader.class.getResource("/fxml/home.fxml");
    public static final URL editNode = ResourceLoader.class.getResource("/fxml/editNode.fxml");
    public static final URL scheduler = ResourceLoader.class.getResource("/fxml/schedule2.fxml");
    public static final URL request = ResourceLoader.class.getResource("/fxml/request.fxml");
    public static final URL createNode = ResourceLoader.class.getResource("/fxml/createNode.fxml");
    public static final URL deleteNodeConfirm = ResourceLoader.class.getResource("/fxml/toast/deleteNodeConfirm.fxml");
    public static final URL saveNodeConfirm = ResourceLoader.class.getResource("/fxml/toast/saveNodeConfirm.fxml");
    public static final URL fulfillConfirm = ResourceLoader.class.getResource("/fxml/toast/requestFulfilledConfirm.fxml");
    public static final URL addEmployeeConfirm = ResourceLoader.class.getResource("/fxml/toast/addEmployeeConfirm.fxml");
    public static final URL deleteEmployeeConfirm = ResourceLoader.class.getResource("/fxml/toast/deleteEmployeeConfirm.fxml");
    public static final URL reservationConfirm = ResourceLoader.class.getResource("/fxml/toast/reservationSuccess.fxml");
    public static final URL invalidTime = ResourceLoader.class.getResource("/fxml/toast/invalidTimeToast.fxml");
    public static final URL impossibleTime = ResourceLoader.class.getResource("/fxml/toast/impossibleTimeToast.fxml");
    public static final URL invalidDate = ResourceLoader.class.getResource("/fxml/toast/invalidDateToast.fxml");
    public static final URL idle = ResourceLoader.class.getResource("/fxml/Idle.fxml");

    public static final URL about = ResourceLoader.class.getResource("/fxml/home/aboutPage.fxml");
    public static final URL credit = ResourceLoader.class.getResource("/fxml/home/creditsPage.fxml");
    public static final URL directionMessage = ResourceLoader.class.getResource("/fxml/home/directionMessage.fxml");
    public static final URL pathFindingSettings = ResourceLoader.class.getResource("/fxml/home/pathfindingSettings.fxml");
    public static final URL searchResults = ResourceLoader.class.getResource("/fxml/home/searchResults.fxml");
    public static final URL adminServices = ResourceLoader.class.getResource("/fxml/home/adminServices.fxml");
    public static final URL confirmScheduler = ResourceLoader.class.getResource("/fxml/confirmReservation.fxml");
    public static final URL calendarPane = ResourceLoader.class.getResource("/fxml/calendarPane.fxml");

    public static final URL superSecretPasswords = ResourceLoader.class.getResource("SuperSecretPasswords.txt");

    public static final URL maintenanceRequest = ResourceLoader.class.getResource("/fxml/request/maintenance_requests.fxml");
    public static final URL avServiceRequest = ResourceLoader.class.getResource("/fxml/request/avservice_requests.fxml");
    public static final URL floristRequest = ResourceLoader.class.getResource("/fxml/request/floristRequest.fxml");
    public static final URL patientInfoRequest = ResourceLoader.class.getResource("/fxml/request/patientInfo.fxml");
    public static final URL interpreterRequest = ResourceLoader.class.getResource("/fxml/request/interpreterRequest.fxml");
    public static final URL sanitationRequest = ResourceLoader.class.getResource("/fxml/request/sanitationRequest.fxml");
    public static final URL securityRequest = ResourceLoader.class.getResource("/fxml/request/securityRequest.fxml");
    public static final URL religiousRequest = ResourceLoader.class.getResource("/fxml/request/religiousRequest.fxml");
    public static final URL ToyRequest = ResourceLoader.class.getResource("/fxml/request/ToyRequest.fxml");
    public static final URL externalTransportRequest = ResourceLoader.class.getResource("/fxml/request/ExternalTransportationRequest.fxml");
    public static final URL giftStoreRequest = ResourceLoader.class.getResource("/fxml/request/giftStoreRequest.fxml");
    public static final URL medicineRequest = ResourceLoader.class.getResource("/fxml/request/medicine_request.fxml");
    public static final URL itRequest = ResourceLoader.class.getResource("/fxml/request/it_request.fxml");
    public static final URL internalTransportRequest = ResourceLoader.class.getResource("/fxml/request/internalTransportRequest.fxml");

    public static final URL edges = ResourceLoader.class.getResource("/csv/edges.csv");
    public static final URL nodes = ResourceLoader.class.getResource("/csv/nodes.csv");
    public static final URL fulfillrequest = ResourceLoader.class.getResource("/fxml/fulfillrequest.fxml");
    public static final URL reservablespaces = ResourceLoader.class.getResource("/csv/reservablespaces.csv");
    public static final URL employees = ResourceLoader.class.getResource("/csv/employees.csv");
    public static final URL employeeEdit = ResourceLoader.class.getResource("/fxml/employeeEdit.fxml");

    public static final URL textingService = ResourceLoader.class.getResource("/SuperSecretPasswords.txt");

    // images
    public static final URL firstFloor = ResourceLoader.class.getResource("/images/F1.png");
    public static final URL secondFloor = ResourceLoader.class.getResource("/images/F2.png");
    public static final URL thirdFloor = ResourceLoader.class.getResource("/images/F3.png");
    public static final URL fourthFloor = ResourceLoader.class.getResource("/images/F4.png");
    public static final URL groundFloor = ResourceLoader.class.getResource("/images/ground.png");
    public static final URL firstLowerFloor = ResourceLoader.class.getResource("/images/L1.png");
    public static final URL secondLowerFloor = ResourceLoader.class.getResource("/images/L2.png");
    public static final URL square = ResourceLoader.class.getResource("/images/square.png");
    public static final URL spike = ResourceLoader.class.getResource("/images/spike.png");
    public static final URL background = ResourceLoader.class.getResource("/images/background.jpg");
    public static final URL adminLogin = ResourceLoader.class.getResource("/fxml/employeeLogin.fxml");

    // direction icons
    public static final URL continue_icon = ResourceLoader.class.getResource("/images/direction_icons/continue.png");
    public static final URL elevator_icon = ResourceLoader.class.getResource("/images/direction_icons/elevator.png");
    public static final URL walking_icon = ResourceLoader.class.getResource("/images/direction_icons/pedestrian-walking.png");
    public static final URL stairs_down_icon = ResourceLoader.class.getResource("/images/direction_icons/stairs-down.png");
    public static final URL stairs_up_icon = ResourceLoader.class.getResource("/images/direction_icons/stairs-up.png");
    public static final URL turn_left_icon = ResourceLoader.class.getResource("/images/direction_icons/turn_left.png");
    public static final URL turn_right_icon = ResourceLoader.class.getResource("/images/direction_icons/turn_right.png");
    public static final URL turn_sharp_left_icon = ResourceLoader.class.getResource("/images/direction_icons/turn_sharp_left.png");
    public static final URL turn_sharp_right_icon = ResourceLoader.class.getResource("/images/direction_icons/turn_sharp_right.png");
    public static final URL turn_slight_left_icon = ResourceLoader.class.getResource("/images/direction_icons/turn_slight_left.png");
    public static final URL turn_slight_right_icon = ResourceLoader.class.getResource("/images/direction_icons/turn_slight_right.png");
    public static final URL uturn_icon = ResourceLoader.class.getResource("/images/direction_icons/uturn.png");

    //themes
    public static final URL default_style = ResourceLoader.class.getResource("/default.css");
    public static final URL high_contrast_style = ResourceLoader.class.getResource("/HighContrast.css");
    public static final URL night_style = ResourceLoader.class.getResource("/night.css");


    public static final ResourceBundle dfBundle = ResourceBundle.getBundle("strings", Locale.getDefault());
    public static final ResourceBundle enBundle = ResourceBundle.getBundle("strings", new Locale("en"));
    public static final ResourceBundle esBundle = ResourceBundle.getBundle("strings", new Locale("es"));
}
