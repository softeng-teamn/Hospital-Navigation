package service;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class  ResourceLoader {

    public static final URL home = service.ResourceLoader.class.getResource("/fxml/home.fxml");
    public static final URL editNode = service.ResourceLoader.class.getResource("/fxml/editNode.fxml");
    public static final URL scheduler = service.ResourceLoader.class.getResource("/fxml/schedule.fxml");
    public static final URL request = service.ResourceLoader.class.getResource("/fxml/request.fxml");
    public static final URL createNode = service.ResourceLoader.class.getResource("/fxml/createNode.fxml");
    public static final URL deleteNodeConfirm = service.ResourceLoader.class.getResource("/fxml/toast/deleteNodeConfirm.fxml");
    public static final URL saveNodeConfirm = service.ResourceLoader.class.getResource("/fxml/toast/saveNodeConfirm.fxml");

    public static final URL directionMessage = service.ResourceLoader.class.getResource("/fxml/home/directionMessage.fxml");
    public static final URL pathFindingSettings = service.ResourceLoader.class.getResource("/fxml/home/pathfindingSettings.fxml");
    public static final URL searchResults = service.ResourceLoader.class.getResource("/fxml/home/searchResults.fxml");
    public static final URL adminServices = service.ResourceLoader.class.getResource("/fxml/home/adminServices.fxml");
    public static final URL faceDetect = service.ResourceLoader.class.getResource("/fxml/faceDetect.fxml");

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


    public static final ResourceBundle dfBundle = ResourceBundle.getBundle("strings", Locale.getDefault());
    public static final ResourceBundle enBundle = ResourceBundle.getBundle("strings", new Locale("en"));
    public static final ResourceBundle esBundle = ResourceBundle.getBundle("strings", new Locale("es"));

    public static final URL testFace = service.ResourceLoader.class.getResource("/public/testFace.png");
    public static final URL knownFace = service.ResourceLoader.class.getResource("/public/known.png");
    public static final URL train = service.ResourceLoader.class.getResource("/public/train");
    // classifiers
    public static final URL haarClassifier = service.ResourceLoader.class.getResource("/haarcascades/haarcascade_frontalface_alt.xml");
//    public static final File haarFile = new File(haarURL.getFile());
//    public static final URL lbpcascades = service.ResourceLoader.class.getResource("/lbpcascades/lbpcascade_frontalface.xml");
}