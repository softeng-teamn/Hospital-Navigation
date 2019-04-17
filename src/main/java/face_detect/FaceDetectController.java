//package face_detect;
//
//import com.jfoenix.controls.JFXButton;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.stage.Stage;
//import org.opencv.core.*;
//import org.opencv.objdetect.CascadeClassifier;
//import org.opencv.videoio.VideoCapture;
//import service.ResourceLoader;
//import service.StageManager;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import static employee.utils.Utils.mat2Image;
//import static employee.utils.Utils.onFXThread;
//
//
//public class FaceDetectController {
//
//    @FXML
//    private ImageView originalFrame;
//    @FXML
//    private JFXButton cameraButton;
//    @FXML
//    private Label label_msg;
//
//    // Thread for face detection
//    Thread faceThread;
//
//    // a timer for acquiring the video stream
//    private ScheduledExecutorService timer;
//    // the OpenCV object that performs the video capture
//    private VideoCapture capture;
//    // a flag to change the button behavior
//    private boolean cameraActive;
//
//    // face cascade classifier
//    private CascadeClassifier faceCascade;
//    private int absoluteFaceSize;
//
//    // when to save face image
//    private boolean canSaveFace = true;
//
//    @FXML
//    void initialize() {
//        this.capture = new VideoCapture();
//        this.faceCascade = new CascadeClassifier();
//        this.absoluteFaceSize = 0;
//        // set a fixed width for the frame
//        originalFrame.setFitWidth(600);
//        // preserve image ratio
//        originalFrame.setPreserveRatio(true);
//        // set classifier
//        this.faceCascade.load(ResourceLoader.haarClassifier.getPath());
//    }
//
//    @FXML
//    void startCamera(ActionEvent e) {
//        if (!this.cameraActive) {
//            // start video capture
//            this.capture.open(0);
//            // check if video stream is available
//            if (this.capture.isOpened()) {
//                this.cameraActive = true;
//                // grab a frame every 33ms (30 frames/sec)
//                Runnable frameGrabber = new Runnable() {
//                    @Override
//                    public void run() {
//                        // process a single frame
//                        Mat frame = grabFrame();
//                        // convert and show the frame
//                        Image imageToShow = mat2Image(frame);
//                        updateImageView(originalFrame, imageToShow);
//                    }
//                };
//                this.timer = Executors.newSingleThreadScheduledExecutor();
//                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
//
//                // update the button content
//                this.cameraButton.setText("Stop Scan");
//                // change label
//                this.label_msg.setText("Scanning...");
//            } else {
//                // log the error
//                System.err.println("Impossible to open the camera connection...");
//            }
//        } else {
//            // the camera is not active at this point
//            this.cameraActive = false;
//            // update again the button content
//            this.cameraButton.setText("Start Scan");
//            // change label
//            this.label_msg.setText("");
//            // allow retry of face detection
//            this.canSaveFace = true;
//
//            // stop the timer
//            this.stopAcquisition();
//        }
//    }
//
//    private void updateImageView(ImageView view, Image image) {
//        onFXThread(view.imageProperty(), image);
//    }
//
//    private Mat grabFrame() {
//        Mat frame = new Mat();
//        if (this.capture.isOpened()) {
//            try {
//                this.capture.read(frame);
//                if (!frame.empty()) {
//                    // face detection
//                    this.detectAndDisplay(frame);
//                }
//            } catch (Exception e) {
//                System.err.println("Exception during the image elaboration: " + e);
//            }
//        }
//        return frame;
//    }
//
//    // looks for faces
//    private void detectAndDisplay(Mat frame) {
//        MatOfRect faces = new MatOfRect();
//        Mat grayFrame = new Mat();
//
//        // convert frame to gray scale
//        Imgproc.cvtColor(frame,grayFrame,Imgproc.COLOR_BGR2GRAY);
//        // equalizes the frame histogram to improve results
//        Imgproc.equalizeHist(grayFrame, grayFrame);
//
//        // compute minimum face size (20% of the frame height)
//        if (this.absoluteFaceSize == 0) {
//            int height = grayFrame.rows();
//            if (Math.round(height * 0.2f) > 0) {
//                this.absoluteFaceSize = Math.round(height * 0.2f);
//            }
//        }
//        // detect faces
//        this.faceCascade.detectMultiScale(grayFrame,faces,1.1,2,0| Objdetect.CASCADE_SCALE_IMAGE,
//                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
//        Rect[] facesArray = faces.toArray();
//        // an actual face was found!
//        Rect rectCrop = null;
//        for (int i = 0; i < facesArray.length; i++) {
//            Rect rect = facesArray[i];
//            Imgproc.rectangle(frame, new Point(rect.x, rect.y),
//                    new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0,255,0), 2);
////               Imgproc.rectangle(frame,facesArray[i].tl(),facesArray[i].br(),new Scalar(0,255,0),3);
//            rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
//        }
//            if (canSaveFace && rectCrop != null) {
//                canSaveFace = false;
//                Mat markedImage = new Mat(frame, rectCrop);
//                System.out.println("LETS SAVE THIS MAN'S FACE TO A FILE!!!");
//                Image faceImg = mat2Image(markedImage);
//                File faceFile = new File(ResourceLoader.testFace.getPath());
//                BufferedImage bImage = SwingFXUtils.fromFXImage(faceImg, null);
//                try {
//                    ImageIO.write(bImage, "png", faceFile);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//                FindMatch findMatch = new FindMatch(new MyCallback() {
//                    @Override
//                    public void callback(boolean b) {
//                        if (b) {
//                            System.out.println("MATCH FOUND");
//                            Platform.runLater(() -> {
//                                label_msg.setText("Hello Tyler");
//                            });
//                        } else {
//                            System.out.println("NOPE....");
//                        }
//                    }
//                });
//                Thread thread = new Thread(findMatch);
//                thread.start();
////                try {
//////                    thread.join();
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//
////                    if (isMatch()) {
////                        authenticated();
////                    }
////                    checkFace();
//            }
//                // Saving the output image
//
//    }
//
//    void onFoundMatch() {
//
//    }
//
//    // compares testFace.png with every other image
//    //
//    boolean checkFace() {
//        // iterate through all employees that have pictures
//        // get their picture from the database and write to public/2-known_1.png
//        // run curl command to faceX
//        return true;
//
//
//        // if MATCH
//        //      break & call authenticate method (not created yet)
//    }
//
//    // pass in employee id and say "Hello!"
//    void authenticated() {
//        System.out.println("ITS A MATCH!");
//    }
//
//    void stopAcquisition() {
//        if (this.timer != null && !this.timer.isShutdown()) {
//            try {
//                // stop timer
//                this.timer.shutdown();
//                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException e) {
//                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
//            }
//        }
//        if (this.capture.isOpened()) {
//            // release the camera
//            this.capture.release();
//        }
//    }
//
//    @FXML
//    void cancelAction() throws Exception {
//        this.stopAcquisition();
//        showLogin();
//    }
//
//    void showLogin() throws Exception {
//        Stage stage = (Stage) cameraButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(ResourceLoader.adminLogin);
//        StageManager.changeExistingWindow(stage,root,"Login");
//    }
//
//    void showHome() throws Exception {
//        Stage stage = (Stage) cameraButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(ResourceLoader.home);
//        StageManager.changeExistingWindow(stage,root,"Home");
//    }
//
//    public void setClosed() {
//        this.stopAcquisition();
//    }
//
//}
