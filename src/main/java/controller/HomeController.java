package controller;

import javafx.fxml.FXML;
import model.HomeState;

import java.util.Observable;

public class HomeController extends Observable {

    private HomeState state;

    @FXML
    private MapView mapViewController;
    @FXML
    private SearchResults searchResultsController;
    @FXML
    private TopNav topNavController;

    @FXML
    void initialize() {

    }



    // DUMMY METHOD
    public void changeLoggedIn() {
        this.state.setLoggedIn(!state.isLoggedIn());
        setChanged();
        notifyObservers(state);
    }

}