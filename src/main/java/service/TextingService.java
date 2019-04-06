package service;
// Install the Java helper library from twilio.com/docs/libraries/java
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TextingService {
    // Find your Account Sid and Auth Token at twilio.com/console
    public static final String ACCOUNT_SID =
            "AC71f0446a3ec9e6790458fa5f4739730d";
    public static final String AUTH_TOKEN =
            "2bcd148080c10578255d1091762d0f07";
    String phone;

    //Add map when available
    TextingService(String phone){
        this.phone = phone;
    }

    public void textMap(){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
                .creator(new com.twilio.type.PhoneNumber(phone), // to
                        new com.twilio.type.PhoneNumber("+19783961788"), // from
                        "Greetings Father")
                .create();

        System.out.println(message.getSid());
    }
}
