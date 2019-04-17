package service;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * FXML loader
 */
public class  ResourceLoader {

    public static final URL home = service.ResourceLoader.class.getResource("/fxml/home.fxml");
    public static final URL editNode = service.ResourceLoader.class.getResource("/fxml/editNode.fxml");
    public static final URL scheduler = service.ResourceLoader.class.getResource("/fxml/schedule2.fxml");
    public static final URL request = service.ResourceLoader.class.getResource("/fxml/request.fxml");
    public static final URL createNode = service.ResourceLoader.class.getResource("/fxml/createNode.fxml");
    public static final URL deleteNodeConfirm = service.ResourceLoader.class.getResource("/fxml/toast/deleteNodeConfirm.fxml");
    public static final URL saveNodeConfirm = service.ResourceLoader.class.getResource("/fxml/toast/saveNodeConfirm.fxml");

    public static final URL about = service.ResourceLoader.class.getResource("/fxml/home/aboutPage.fxml");
    public static final URL directionMessage = service.ResourceLoader.class.getResource("/fxml/home/directionMessage.fxml");
    public static final URL pathFindingSettings = service.ResourceLoader.class.getResource("/fxml/home/pathfindingSettings.fxml");
    public static final URL searchResults = service.ResourceLoader.class.getResource("/fxml/home/searchResults.fxml");
    public static final URL adminServices = service.ResourceLoader.class.getResource("/fxml/home/adminServices.fxml");
    public static final URL confirmScheduler = service.ResourceLoader.class.getResource("/fxml/confirmReservation.fxml");

    public static final URL superSecretPasswords = service.ResourceLoader.class.getResource("SuperSecretPasswords.txt");


    public static final URL maintenanceRequest = service.ResourceLoader.class.getResource("/fxml/request/maintenance_requests.fxml");
    public static final URL avServiceRequest = service.ResourceLoader.class.getResource("/fxml/request/avservice_requests.fxml");
    public static final URL floristRequest = service.ResourceLoader.class.getResource("/fxml/request/floristRequest.fxml");
    public static final URL patientInfoRequest = service.ResourceLoader.class.getResource("/fxml/request/patientInfo.fxml");
    public static final URL interpreterRequest = service.ResourceLoader.class.getResource("/fxml/request/interpreterRequest.fxml");
    public static final URL sanitationRequest = service.ResourceLoader.class.getResource("/fxml/request/sanitationRequest.fxml");
    public static final URL securityRequest = service.ResourceLoader.class.getResource("/fxml/request/securityRequest.fxml");
    public static final URL religiousRequest = service.ResourceLoader.class.getResource("/fxml/request/religiousRequest.fxml");
    public static final URL ToyRequest = service.ResourceLoader.class.getResource("/fxml/request/ToyRequest.fxml");
    public static final URL externalTransportRequest = service.ResourceLoader.class.getResource("/fxml/request/ExternalTransportationRequest.fxml");
    public static final URL giftStoreRequest = service.ResourceLoader.class.getResource("/fxml/request/giftStoreRequest.fxml");
    public static final URL medicineRequest = service.ResourceLoader.class.getResource("/fxml/request/medicine_request.fxml");
    public static final URL itRequest = service.ResourceLoader.class.getResource("/fxml/request/it_request.fxml");
    public static final URL internalTransportRequest = service.ResourceLoader.class.getResource("/fxml/request/internalTransportRequest.fxml");

    public static final URL edges = service.ResourceLoader.class.getResource("/csv/edges.csv");
    public static final URL nodes = service.ResourceLoader.class.getResource("/csv/nodes.csv");
    public static final URL fulfillrequest = service.ResourceLoader.class.getResource("/fxml/fulfillrequest.fxml");
    public static final URL reservablespaces = service.ResourceLoader.class.getResource("/csv/reservablespaces.csv");
    public static final URL employees = service.ResourceLoader.class.getResource("/csv/employees.csv");
    public static final URL employeeEdit = service.ResourceLoader.class.getResource("/fxml/employeeEdit.fxml");


    public static final URL textingService = service.ResourceLoader.class.getResource("/SuperSecretPasswords.txt");

    // images
    public static final URL firstFloor = service.ResourceLoader.class.getResource("/images/F1.png");
    public static final URL secondFloor = service.ResourceLoader.class.getResource("/images/F2.png");
    public static final URL thirdFloor = service.ResourceLoader.class.getResource("/images/F3.png");
    public static final URL groundFloor = service.ResourceLoader.class.getResource("/images/ground.png");
    public static final URL firstLowerFloor = service.ResourceLoader.class.getResource("/images/L1.png");
    public static final URL secondLowerFloor = service.ResourceLoader.class.getResource("/images/L2.png");
    public static final URL adminLogin = service.ResourceLoader.class.getResource("/fxml/employeeLogin.fxml");

    // direction icons
    public static final URL continue_icon = service.ResourceLoader.class.getResource("/images/direction_icons/continue.png");
    public static final URL elevator_icon = service.ResourceLoader.class.getResource("/images/direction_icons/elevator.png");
    public static final URL walking_icon = service.ResourceLoader.class.getResource("/images/direction_icons/pedestrian-walking.png");
    public static final URL stairs_down_icon = service.ResourceLoader.class.getResource("/images/direction_icons/stairs-down.png");
    public static final URL stairs_up_icon = service.ResourceLoader.class.getResource("/images/direction_icons/stairs-up.png");
    public static final URL turn_left_icon = service.ResourceLoader.class.getResource("/images/direction_icons/turn_left.png");
    public static final URL turn_right_icon = service.ResourceLoader.class.getResource("/images/direction_icons/turn_right.png");
    public static final URL turn_sharp_left_icon = service.ResourceLoader.class.getResource("/images/direction_icons/turn_sharp_left.png");
    public static final URL turn_sharp_right_icon = service.ResourceLoader.class.getResource("/images/direction_icons/turn_sharp_right.png");
    public static final URL turn_slight_left_icon = service.ResourceLoader.class.getResource("/images/direction_icons/turn_slight_left.png");
    public static final URL turn_slight_right_icon = service.ResourceLoader.class.getResource("/images/direction_icons/turn_slight_right.png");
    public static final URL uturn_icon = service.ResourceLoader.class.getResource("/images/direction_icons/uturn.png");

    public static final ResourceBundle dfBundle = ResourceBundle.getBundle("strings", Locale.getDefault());
    public static final ResourceBundle enBundle = ResourceBundle.getBundle("strings", new Locale("en"));
    public static final ResourceBundle esBundle = ResourceBundle.getBundle("strings", new Locale("es"));
}
