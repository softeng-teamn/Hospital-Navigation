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
    String[] secrets;
    private void readLineByLineJava(URL filePath)
    {
        BufferedReader reader = null;
        StringBuilder contentBuilder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(ResourceLoader.edges.openStream(), "UTF-8"));
            secrets[0] = reader.readLine();
            secrets[1] = reader.readLine();
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public final String ACCOUNT_SID =
            secrets[0];
    public final String AUTH_TOKEN =
            secrets[1];

    public void textMap(String phone, String mapUrl){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
                .creator(new com.twilio.type.PhoneNumber(phone), // to
                        new com.twilio.type.PhoneNumber("+19783961788"), // from
                        mapUrl)
                .create();

        System.out.println(message.getSid());
    }
}
