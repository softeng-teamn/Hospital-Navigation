package service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.testing.RemoteTranslateHelper;

import java.io.IOException;

public class GoogleTranslateService {
    private Translate translate;
    public GoogleTranslateService(){
        // Instantiates a client
        GoogleCredentials credentials=null;
        try {
            credentials = ServiceAccountCredentials.fromStream(ResourceLoader.google_translate.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }



        translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public String TranslateFromEnglish(String orginText,String langKey){
        Translation translation = translate.translate(
                orginText,
                TranslateOption.sourceLanguage("en"),
                TranslateOption.targetLanguage(langKey));
        return translation.getTranslatedText();
    }

}
