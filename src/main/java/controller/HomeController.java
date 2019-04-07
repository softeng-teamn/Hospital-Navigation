package controller;

import com.google.common.eventbus.EventBus;
import com.jfoenix.controls.*;
import com.sun.javafx.font.Glyph;
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
import model.EventBusFactory;
import model.HomeState;

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