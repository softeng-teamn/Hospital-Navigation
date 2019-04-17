package web_server;

import service.ResourceLoader;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class WebServer {

    public static void run() {
        staticFiles.location("/public");
//        staticFiles.header("Content-Type","image/png");
        get("/known", (req, res) -> {
            Path path = Paths.get(ResourceLoader.testFace.toURI());
            byte[] data = null;
            try {
                data = Files.readAllBytes(path);
            } catch (Exception e) {
                System.err.println("Can't read bytes ... " + e);
            }
            HttpServletResponse raw = res.raw();
            res.header("Content-Disposition", "attachment; filename=image.png");
            res.type("application/force-download");
            try {
                raw.getOutputStream().write(data);
                raw.getOutputStream().flush();
                raw.getOutputStream().close();
            } catch (Exception e) {
                System.err.println("Trouble with output stream ... " + e);
            }
            return raw;
//            System.out.println(ResourceLoader.tylerFace);
//            BufferedImage in = ImageIO.read(new File(ResourceLoader.tylerFace.getFile()));
//            BufferedImage bi = new BufferedImage(
//                    in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);
//
//            byte[] rawImage = null;
//            try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//                ImageIO.write(bi, "png",baos);
//                baos.flush();
//                rawImage = baos.toByteArray();
//            }
//            res.type("image/png");
//            res.header("Content-Type", "image/png");
//            res.b
//            return rawImage;
        });

    }

//    public static boolean isMatch(){
//
//
//    }


}
