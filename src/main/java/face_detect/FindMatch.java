package face_detect;

import employee.MyCallback;

import static org.toilelibre.libe.curl.Curl.$;

public class FindMatch implements Runnable {
    private MyCallback c;

    public FindMatch(MyCallback c) {
        this.c = c;
    }

    @Override
    public void run() {
        System.out.println("calling curl command");
        String res = $("curl --location --request POST \"http://www.facexapi.com/compare_faces?face_det=1\"" +
                "  --header \"user_id: 41a40b0480374c004cac\"" +
                "  --header \"user_key: 086eba4443504ab026fe\"" +
                "  --form \"img_1=https://teamnhospital.serveo.net/testFace.png\"" +
                "  --form \"img_2=https://teamnhospital.serveo.net/known.png\"");
        System.out.println(res.toString());
        try {
            String[] myResponse = res.split("\"");
            String val = myResponse[3];
            System.out.println("HERE IS THE VALUE");
            System.out.println(val);
            Boolean value = Double.valueOf(val) >= 0.4;
            c.callback(value);
        } catch (Exception e) {
            c.callback(false);
        }

    }
}
