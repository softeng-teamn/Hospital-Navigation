package employee;

import face_detect.FindMatch;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import service.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static employee.utils.Utils.mat2Image;

// Facial recognition object
public class Face {

    private CascadeClassifier faceCascade;
    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;
    private int absoluteFaceSize;
    private boolean canSaveFace = true;
    private MyCallback callback;


    public Face(String employeeID, MyCallback callback) {
        this.callback = callback;
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;
        // set classifier
        this.faceCascade.load(ResourceLoader.haarClassifier.getPath());

    }

    public void isMatch() {
        // start video capture
        this.capture.open(0);
        if (this.capture.isOpened()) {
            // grab a frame every 33ms (30 frames/sec)
            Runnable frameGrabber = new Runnable() {
                @Override
                public void run() {
                    // process a single frame
                    Mat frame = grabFrame();
                    // convert and show the frame
//                    Image imageToShow = mat2Image(frame);
                }
            };
            this.timer = Executors.newSingleThreadScheduledExecutor();
            this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
        } else {
            // log the error
            System.err.println("Impossible to open the camera connection...");
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {
                    // face detection
                    this.detectAndSave(frame);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return frame;
    }

    // looks for faces
    private void detectAndSave(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert frame to gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalizes the frame histogram to improve results
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height)
        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }
        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
        Rect[] facesArray = faces.toArray();
        // an actual face was found!
        Rect rectCrop = null;
        for (int i = 0; i < facesArray.length; i++) {
            Rect rect = facesArray[i];
//            Imgproc.rectangle(frame, new Point(rect.x, rect.y),
//                    new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0, 255, 0), 2);
//               Imgproc.rectangle(frame,facesArray[i].tl(),facesArray[i].br(),new Scalar(0,255,0),3);
            rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        }
        if (canSaveFace && rectCrop != null) {
            canSaveFace = false;
            Mat markedImage = new Mat(frame, rectCrop);
            System.out.println("LETS SAVE THIS MAN'S FACE TO A FILE!!!");
            Image faceImg = mat2Image(markedImage);
            File faceFile = new File(ResourceLoader.testFace.getPath());
            BufferedImage bImage = SwingFXUtils.fromFXImage(faceImg, null);
            try {
                ImageIO.write(bImage, "png", faceFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            FindMatch findMatch = new FindMatch(new MyCallback() {
                @Override
                public void callback(boolean b) {
                    if (b) {
                        System.out.println("MATCH FOUND");
                    } else {
                        System.out.println("NOPE.... no match");
                    }
                    callback.callback(b);
                }
            });
            Thread thread = new Thread(findMatch);
            thread.start();
            stopCamera();
        }

    }


    public void stopCamera() {
        System.out.println("shutting donwn camera");
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }
        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }
}