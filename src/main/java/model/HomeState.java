package model;

public class HomeState {

    private boolean isLoggedIn; // 'true or false'
    private String loginType; // ex 'Admin' or 'Employee' or 'Staff'
    private String searchQuery;

    void HomeState() {
        isLoggedIn = false;
        loginType = "";
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }


}
