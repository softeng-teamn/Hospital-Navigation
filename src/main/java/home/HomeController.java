package home;

import application_state.ApplicationState;
import application_state.Event;
import application_state.Observer;
import com.jfoenix.controls.JFXDrawer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import map.MapController;
import service.ResourceLoader;

import java.io.*;
import java.util.ArrayList;

/**
 * Controls the main screen of the application
 */
public class HomeController implements Observer {

    private Event event;

    @FXML
    private JFXDrawer drawer;

    StackPane drawerPane = new StackPane();

    /** initializes the main screen
     * @throws IOException if the FXML fails to load
     */
    @FXML
    void initialize() throws IOException {
        // Get the current info and register this as observer
        ApplicationState.getApplicationState().getObservableBus().register("homeController", this);
        ApplicationState currState = ApplicationState.getApplicationState();
        event = ApplicationState.getApplicationState().getObservableBus().getEvent();
        currState.setDefaultStartNode();    // Set the default start
        currState.setEndNode(null);
        MapController.initConnections();

        drawer.setSidePane(drawerPane);
        drawer.setDefaultDrawerSize(480);
        drawer.setResizeContent(true);
        drawer.setOverLayVisible(false);

        readSchedulerSettings();
    }

    /** handles an observed event and changes the screen as appropriate
     * @param newevent event to respond to from the observed class
     */
    @Override
    public void notify(Object newevent) {
        event = (Event) newevent;
        switch (event.getEventName()) {
            case "showText":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showText();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showSearch-start":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationState.getApplicationState().setStartEnd("start");
                        try {
                            showSearch();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showSearch-end":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationState.getApplicationState().setStartEnd("end");
                        try {
                            showSearch();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showAdmin":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showAdmin();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "showEmployee":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showEmployee();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break ;
            case "showPathSetting":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showPathSettings();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                  });
                  break ;
            case "closeDrawer":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        drawer.close();
                    }
                });
                break;
            case "logout":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        drawer.close();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void showAdmin() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.adminServices));
        drawer.open();
    }

    private void showEmployee() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.adminServices));
        drawer.open();
    }

    private void showSearch() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.searchResults));
        drawer.open();
    }

    private void showText() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.directionMessage));
        drawer.open();
    }

    private void showPathSettings() throws IOException {
        drawerPane.getChildren().clear();
        drawerPane.getChildren().add(FXMLLoader.load(ResourceLoader.pathFindingSettings));
        drawer.open();
    }

    /**
     * Read the stored scheduler settings
     */
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private void readSchedulerSettings() {
        String fileName = "./src/main/resources/schedulerSettings.txt";
        ArrayList<String> args = new ArrayList<>();
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                String currArg = "";
                for (int i = 0; i < line.length(); i++) {    // Read the settings from the file, separated by commas
                    String lett = line.substring(i, i+1);
                    if (lett.equals(",")) {
                        args.add(currArg);
                        currArg = "";
                    }
                    else {
                        currArg += lett;
                    }
                }
                args.add(currArg);
            }

            // Close files.
            bufferedReader.close();
            fileReader.close();
        }
        catch(FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            ex.printStackTrace();
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }

        // Set the settings
        ApplicationState currState = ApplicationState.getApplicationState();
        currState.setOpenTime(Integer.parseInt(args.get(0)));
        currState.setOpenTimeStr(args.get(1));
        currState.setOpenTimeMinutes(Integer.parseInt(args.get(2)));
        currState.setCloseTime(Integer.parseInt(args.get(3)));
        currState.setCloseTimeString(args.get(4));
        currState.setCloseTimeMinutes(Integer.parseInt(args.get(5)));
        currState.setTimeStep(Integer.parseInt(args.get(6)));
        currState.setBoundOpenTime(Boolean.parseBoolean(args.get(7)));
        currState.setBoundCloseTime(Boolean.parseBoolean(args.get(8)));
        currState.setBoundMinRes(Boolean.parseBoolean(args.get(9)));
        currState.setSnapToMinutes(Boolean.parseBoolean(args.get(10)));
        currState.setAllowMultidayRes(Boolean.parseBoolean(args.get(11)));
        currState.setAllowRecurringRes(Boolean.parseBoolean(args.get(12)));
        currState.setShowContactInfo(Boolean.parseBoolean(args.get(13)));
    }
}
