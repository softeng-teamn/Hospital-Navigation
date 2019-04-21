package service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import net.swisstech.bitly.BitlyClient;
import net.swisstech.bitly.model.Response;
import net.swisstech.bitly.model.v3.ShortenResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRService {

    /**
     *
     * @param url The URL (spaces will be replaced with dollar signs
     * @param shorten
     * @return
     */
    public static Image generateQRCode(String url, boolean shorten) throws IOException, WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        if (shorten) {
            url = shortenURL(url);
        }

        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 350, 350);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        Image qr = new Image(new ByteArrayInputStream(pngData));
        return qr;
    }

    public static String shortenURL(String url) {
        if (url.length() > 2000) return url;
        BitlyClient client = new BitlyClient("bcba608ae5c6045d223241662c704f38c52930e4");
        Response<ShortenResponse> resp = client.shorten()
                .setLongUrl(url)
                .call();
        return resp.data.url;
    }

}
