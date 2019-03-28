package service;

import java.io.File;
import java.net.URL;

public class ResourceLoader {

    public static final URL home = service.ResourceLoader.class.getResource("/home.fxml");
    public static final URL mapEdit = service.ResourceLoader.class.getResource("/mapEdit.fxml");
    public static final URL scheduler = service.ResourceLoader.class.getResource("/schedule.fxml");
    public static final URL request = service.ResourceLoader.class.getResource("/request.fxml");
    public static final URL edges = service.ResourceLoader.class.getResource("/edges.csv");
    public static final URL nodes = service.ResourceLoader.class.getResource("/nodes.csv");
//        service.ResourceLoader.class.getResource("/nodes.csv");

}
