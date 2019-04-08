package service;

import java.net.URL;

public class ResourceLoader {

    public static final URL home = service.ResourceLoader.class.getResource("/home.fxml");
    public static final URL scheduler = service.ResourceLoader.class.getResource("/schedule.fxml");
    public static final URL request = service.ResourceLoader.class.getResource("/request.fxml");
    public static final URL createNode = service.ResourceLoader.class.getResource("/createNode.fxml");
    public static final URL maintenanceRequest = service.ResourceLoader.class.getResource("/requests/maintenance_requests.fxml");
    public static final URL floristRequest = service.ResourceLoader.class.getResource("/requests/floristRequest.fxml");
    public static final URL patientInfoRequest = service.ResourceLoader.class.getResource("/requests/patientInfo.fxml");
    public static final URL interpreterRequest = service.ResourceLoader.class.getResource("/requests/interpreterRequest.fxml");
    public static final URL sanitationRequest = service.ResourceLoader.class.getResource("/requests/sanitationRequest.fxml");
    public static final URL ToyRequest = service.ResourceLoader.class.getResource("/Requests/ToyRequest.fxml");
    public static final URL securityRequest = service.ResourceLoader.class.getResource("/requests/securityRequest.fxml");
    public static final URL giftStoreRequest = service.ResourceLoader.class.getResource("/Requests/giftStoreRequest.fxml");
    public static final URL medicineRequest = service.ResourceLoader.class.getResource("/requests/medicine_request.fxml");
    public static final URL itRequest = service.ResourceLoader.class.getResource("/requests/it_request.fxml");

    public static final URL edges = service.ResourceLoader.class.getResource("/edges.csv");
    public static final URL nodes = service.ResourceLoader.class.getResource("/nodes.csv");
    public static final URL fulfillrequest = service.ResourceLoader.class.getResource("/fulfillrequest.fxml");
    public static final URL internalTransportRequest = service.ResourceLoader.class.getResource("/requests/internalTransportRequest.fxml");
    public static final URL reservablespaces = service.ResourceLoader.class.getResource("/reservablespaces.csv");
    public static final URL employees = service.ResourceLoader.class.getResource("/employees.csv");


    // Images
    public static final URL firstFloor = service.ResourceLoader.class.getResource("/Images/01_thefirstfloor.png");
    public static final URL calendarIcon = service.ResourceLoader.class.getResource("/Images/calendar.png");
    public static final URL pencilIcon = service.ResourceLoader.class.getResource("/Images/pencil.jpg");
    public static final URL wrenchICon = service.ResourceLoader.class.getResource("/Images/wrench.jpg");
    public static final URL adminLogin = service.ResourceLoader.class.getResource("/administratorLogin.fxml");
}
