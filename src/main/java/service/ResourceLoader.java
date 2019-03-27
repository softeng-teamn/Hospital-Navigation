package service;

import java.net.URL;

public class ResourceLoader {

    public static URL home = service.ResourceLoader.class.getResource("/home.fxml");
    public static URL mapEdit = service.ResourceLoader.class.getResource("/mapEdit.fxml");
    public static URL scheduler = service.ResourceLoader.class.getResource("/schedule.fxml");
    public static URL request = service.ResourceLoader.class.getResource("/request.fxml");

}
