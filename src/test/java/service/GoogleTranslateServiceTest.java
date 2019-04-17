package service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testclassifications.FastTest;
import testclassifications.SlowTest;
import testclassifications.SpecialTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class GoogleTranslateServiceTest {

    GoogleTranslateService googleTranslateService;

    @Before
    public void setup() throws Exception{
        googleTranslateService = new GoogleTranslateService();

    }

    @Test
    @Category(SpecialTest.class)
    public void properTranslation(){
        String translated = googleTranslateService.TranslateFromEnglish("This is a Test", "es");
        assertEquals(translated,"Esto es una prueba");


    }
}
