package model;

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

public class ElevatorCon {
    private static HttpURLConnection con;

    //default constructor
    public ElevatorCon() {
    }

    /**
     * @param elevator name of elevator to tell
     * @param floorNum the floor the specified elevator should go to
     * @param time     what time is should get to the elevator at
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    //tell the elevator to go to this floor at this time, will hold for 30s
    public void postFloor(String elevator, String floorNum, GregorianCalendar time) throws MalformedURLException,
            ProtocolException, IOException {
        switch (floorNum) {
            case "L1":
                floorNum = "-1";
                break;
            case "L2":
                floorNum = "-2";
                break;
            case "00":
                floorNum = "0";
                break;
            case "01":
                floorNum = "1";
                break;
            case "02":
                floorNum = "2";
                break;
            case "03":
                floorNum = "3";
                break;
            default:
                floorNum = "-4";
                break;
        }

        String URL = "https://aldenhallpianos.com/softEngPost.php";
        //change into time since 12
        String t = "" + time.getTimeInMillis() / 1000;

        String urlParameters = "elevator=" + elevator + "&floor=" + floorNum + "&time=" + t + "&isESP=false";
        System.out.println("Posting " + urlParameters);
        post(URL, urlParameters);
    }

    /**
     * @param elevator the name of the elevator to get the position of
     * @return returns a  string containing the floor the elevator is on
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    public String getFloor(String elevator) throws MalformedURLException,
            ProtocolException, IOException {
        String URL = "https://aldenhallpianos.com/softEngGet.php";
        String urlParameters = "elevator=" + elevator + "&isESP=false";
        return post(URL, urlParameters);
    }


    /**
     * @param URL           the url to post to
     * @param urlParameters the parameters of the post request
     * @return returns a string containing the current floor is on
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    private String post(String URL, String urlParameters) throws MalformedURLException,
            ProtocolException, IOException {
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