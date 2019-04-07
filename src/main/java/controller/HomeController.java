package controller;

import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import model.Edge;
import model.EventBusFactory;
import model.MapNode;
import model.Node;
import service.DatabaseService;
import service.PathFindingService;
import service.ResourceLoader;
import service.StageManager;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.ArrayList;

import static controller.Controller.initConnections;

public class HomeController {

    private EventBus eventBus = EventBusFactory.getEventBus();

    @FXML
    private MapView mapViewController;
    @FXML
    private SearchResults searchResultsController;
    @FXML
    private TopNav topNavController;

    @FXML
    void initialize() {
        initConnections();

    }

}