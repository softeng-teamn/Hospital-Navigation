package employee.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import service.ResourceLoader;

import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static employee.utils.Utils.mat2Image;
import static employee.utils.Utils.onFXThread;

public class TakePhotoController {

    private CascadeClassifier faceCascade;
    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive = false;
    private int absoluteFaceSize;
    private boolean canSaveFace = false;

    BufferedImage employeeImage = null;

    @FXML
    private ImageView originalFrame;
    @FXML
    private JFXButton cameraButton, save_btn;
    @FXML
    private MaterialIconView camera_icon;

    @FXML
    void initialize() {
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;
        // set a fixed width for the frame
        originalFrame.setFitWidth(600);
        // preserve image ratio
        originalFrame.setPreserveRatio(true);
        // set classifier
        this.faceCascade.load(ResourceLoader.haarClassifier.getPath());
        save_btn.setDisable(true);
//        save_btn.getScene().getWindow().setOnCloseRequest((e) -> {
//           stopAcquisition();
//        });
    }

    void startCamera() {
        if (!this.cameraActive) {
            // start video capture
            this.capture.open(0);
            // check if video stream is available
            if (this.capture.isOpened()) {
                this.cameraActive = true;
                // grab a frame every 33ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {
                    @Override
                    public void run() {
                        // process a single frame
                        Mat frame = grabFrame();
                        // convert and show the frame
                        Image imageToShow = mat2Image(frame);
                        updateImageView(originalFrame, imageToShow);
                    }
                };
                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;

            // stop the timer
            this.stopAcquisition();
        }
    }

    private void updateImageView(ImageView view, Image image) {
        onFXThread(view.imageProperty(), image);
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {
                    // face detection
                    this.detectAndDisplay(frame);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return frame;
    }

    // looks for faces
    private void detectAndDisplay(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert frame to gray scale
        Imgproc.cvtColor(frame,grayFrame,Imgproc.COLOR_BGR2GRAY);
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
        this.faceCascade.detectMultiScale(grayFrame,faces,1.1,2,0| Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
        Rect[] facesArray = faces.toArray();
        // an actual face was found!
        Rect rectCrop = null;
        for (int i = 0; i < facesArray.length; i++) {
            Rect rect = facesArray[i];
            Imgproc.rectangle(frame, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0,255,0), 2);
//               Imgproc.rectangle(frame,facesArray[i].tl(),facesArray[i].br(),new Scalar(0,255,0),3);
            rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        }
        if (canSaveFace && rectCrop != null) {
            canSaveFace = false;
            Mat markedImage = new Mat(frame, rectCrop);
            System.out.println("LETS SAVE THIS MAN'S FACE TO A FILE!!!");
            Image faceImg = mat2Image(markedImage);
            // set the image field to be saved
            this.employeeImage = SwingFXUtils.fromFXImage(faceImg, null);

            // the camera is not active at this point
            this.cameraActive = false;
            save_btn.setDisable(false);
            stopAcquisition();
        }

    }


    void stopAcquisition() {
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
        canSaveFace = false;
    }

    @FXML
    void takePicture(ActionEvent e) {
        System.out.println("reading take picture: " + canSaveFace);
        if (camera_icon.getGlyphName().equals("CAMERA_ENHANCE")) {
            // turn camera on!
            startCamera();
            camera_icon.setGlyphName("CAMERA");
        } else if (camera_icon.getGlyphName().equals("CAMERA")) {
            // take picture
            this.canSaveFace = true;
            camera_icon.setGlyphName("EDIT");
        } else {
            // restart
            // turn camera on!
            startCamera();
            camera_icon.setGlyphName("CAMERA");
            save_btn.setDisable(true);
        }
    }

    @FXML
    void saveAction(ActionEvent e) {
        // set the image in the home controller
        EmployeeEditController.employeeImage = this.employeeImage;
        closeWindow();
    }

    @FXML
    void cancelAction() throws Exception {
        this.stopAcquisition();
        closeWindow();
    }

    public void closeWindow() {
        Stage stage = (Stage)camera_icon.getScene().getWindow();
        stage.close();
    }

}
