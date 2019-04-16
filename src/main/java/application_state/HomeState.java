//package application_state;
//
//import java.util.Timer;
//
//import static application_state.ApplicationState.getApplicationState;
//
//public class HomeState {    // todo: is this class used for anything?
//
//    private boolean isLoggedIn; // 'true or false'
//    private String loginType; // ex 'Admin' or 'Employee' or 'Staff'
//    private String searchQuery;
//    private Timer timer;
//
//    void HomeState() {
//        isLoggedIn = false;
//        loginType = "";
//    }
//
//    public boolean isLoggedIn() {
//        return isLoggedIn;
//    }
//
//    public void setLoggedIn(boolean loggedIn) {
//        isLoggedIn = loggedIn;
//    }
//
//    public void timeOut(){
//        setLoggedIn(false);
//    }
//    public String getLoginType() {
//        return loginType;
//    }
//
//    public void setLoginType(String loginType) {
//        this.loginType = loginType;
//    }
//
//
//}
