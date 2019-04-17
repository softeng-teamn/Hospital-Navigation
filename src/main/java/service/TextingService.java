package service;
// Install the Java helper library from twilio.com/docs/libraries/java
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.derby.impl.store.access.UTF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.apache.commons.codec.CharEncoding.UTF_8;

/**
 * controls texting functionality
 */
public class TextingService {
    // Find your Account Sid and Auth Token at twilio.com/console
    private String[] secrets= new String[2];
    private String ACCOUNT_SID = "toBeReplaced";
    private String AUTH_TOKEN = "toBeReplaced";

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line =br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public TextingService(){
        try{
            URLConnection urlConnection = ResourceLoader.textingService.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            String data = readFromInputStream(inputStream);
            System.out.println(data);
            this.secrets = data.split("-",2);
            this.ACCOUNT_SID = secrets[0];
            this.AUTH_TOKEN = secrets[1];
        }
        catch(IOException IE){
            System.out.println("IOEXCEPTION TRIGGERED");
        }
    }

    public void textMap(String phone, String toSend){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
                .creator(new com.twilio.type.PhoneNumber(phone), // to
                        new com.twilio.type.PhoneNumber("+19783961788"), // from
                        toSend)
                .create();

        System.out.println(message.getSid());
    }


    public static void main(String[] args) {
        TextingService potato = new TextingService();
        potato.textMap("","");
    }

}
