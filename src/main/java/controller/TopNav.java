package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.HomeState;

import java.util.Observable;
import java.util.Observer;

public class TopNav implements Observer {

    public HomeState state;
    public HomeController controller;

    @FXML
    void showAdminLogin(ActionEvent e) {

    }

    @FXML
    void showFulfillRequest(ActionEvent e) {

    }

    @FXML
    void showSchedule(ActionEvent e) {

    }


    @FXML
    void showRequest(ActionEvent e) {

    }

    public void searchChange() {
        String str = "room4.5";
        state.setLoginType(str);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("THIS STATE JUST CHANGED ABOVE ME");
        HomeState newState = (HomeState) arg;
        System.out.println(newState.isLoggedIn());
    }

}
