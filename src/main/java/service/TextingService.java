package service;
// Install the Java helper library from twilio.com/docs/libraries/java
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TextingService {
    // Find your Account Sid and Auth Token at twilio.com/console
    private String[] secrets= new String[2];
    private void readLineByLineJava(URL filePath)
    {
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(ResourceLoader.textingService.openStream(), StandardCharsets.UTF_8));
            secrets[0] = reader.readLine();
            secrets[1] = reader.readLine();
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private final String ACCOUNT_SID =
            "AC71f0446a3ec9e6790458fa5f4739730d";
    private final String AUTH_TOKEN =
            "2bcd148080c10578255d1091762d0f07";

    public void textMap(String phone, String mapUrl){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
                .creator(new com.twilio.type.PhoneNumber(phone), // to
                        new com.twilio.type.PhoneNumber("+19783961788"), // from
                        mapUrl)
                .create();

        System.out.println(message.getSid());
    }


    public static void main(String[] args) {
        TextingService potato = new TextingService();
        potato.textMap("+19787298044","Test");
    }

}
