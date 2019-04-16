package elevator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Manages the elevator's connection to the online database
 */
public class ElevatorConnection {
    private static HttpURLConnection con;
    private String teamName;

    //for other teams
    public ElevatorConnection(String teamName) {
        this.teamName = teamName;
    }

    //for our team
    public ElevatorConnection(){
        this.teamName = "N";
    }


    /**
     * Default for postFloor
     * @param elevator name of elevator to tell
     * @param floorNum what time the elevator should be called
     * @throws MalformedURLException if the url is invalid
     * @throws ProtocolException if there is no WiFi connection
     * @throws IOException if the file is invalid
     */
    public void postFloor(String elevator, String floorNum)throws IOException{
        GregorianCalendar time = new GregorianCalendar();
        postFloor(elevator, floorNum, time);
    }

    /** calls an elevator to go to the specific floor at the given time, will hold for 30 seconds
     * @param elevator name of elevator to tell
     * @param floorNum the floor the specified elevator should go to
     * @param time     what time is should get to the elevator at
     * @throws IOException if the file is invalid
     */
    public void postFloor(String elevator, String floorNum, GregorianCalendar time) throws IOException {
        switch (floorNum) {
            case "L1":
                floorNum = "-1";
                break;
            case "L2":
                floorNum = "-2";
                break;
            case "0G":
                floorNum = "0";
                break;
            case " G":
                floorNum = "0";
                break;
            default:
                floorNum = floorNum.substring(floorNum.length() - 1);//last num will be floor
                break;
        }

        String URL = "https://aldenhallpianos.com/softEngPost.php";
        //change into time since 12
        String t = "" + time.getTimeInMillis() / 1000;

        String urlParameters = "elevator=" + elevator + "&floor=" + floorNum +
                "&time=" + t + "&team=" + this.teamName;
        //System.out.println("Posting " + urlParameters);
        post(URL, urlParameters);
    }

    /**
     * @param elevator the name of the elevator to get the position of
     * @return returns a  string containing the floor the elevator is on
     * @throws IOException if the file is invalid
     */
    public String getFloor(String elevator) throws IOException {
        String URL = "https://aldenhallpianos.com/softEngGet.php";
        String urlParameters = "elevator=" + elevator + "&isESP=false";
        return post(URL, urlParameters);
    }


    /**
     * @param URL           the url to post to
     * @param urlParameters the parameters of the post service_request
     * @return returns a string containing the current floor is on
     * @throws IOException if the file is invalid
     */
    private String post(String URL, String urlParameters) throws IOException {
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        String floor = "-";
        try {

            URL myurl = new URL(URL);
            con = (HttpURLConnection) myurl.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            floor = content.toString().trim();//remove white space

        } finally {

            con.disconnect();
        }
        return floor;
    }
}