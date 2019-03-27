package service;

import java.net.URL;

public class ResourceLoader {

    public static final URL home = service.ResourceLoader.class.getResource("/home.fxml");
    public static final URL mapEdit = service.ResourceLoader.class.getResource("/mapEdit.fxml");
    public static final URL scheduler = service.ResourceLoader.class.getResource("/schedule.fxml");
    public static final URL request = service.ResourceLoader.class.getResource("/request.fxml");

}
