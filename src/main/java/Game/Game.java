import game.Enemy;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;


public class Game extends Application
{
    int x = 250;
    int y = 700 - 128;
    int timeinair = 0;
    double t = 1;
    double up = 0;
    boolean moveDown = false;
    boolean scoreDidTrigger = false;
    final int HEIGHT = 700;
    final int WIDTH = 1000;
    final int JUMPVALUE = 8;
    int JUMPTIME = 50;
    double speedCounter = 3;
    int score = 0;
    ArrayList<Enemy> enemyList = new ArrayList<>();
    Text scoreText = new Text();
    /*public static void main(String[] args)
    {
        launch(args);
    }*/

    public void execute(Stage theStage) throws IOException {
        StackPane root = new StackPane();
        root.setPrefSize(600, 800);
        Canvas canvas = new Canvas();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image original = new Image(ResourceLoader.background.openStream());

        root.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setWidth(newValue.getWidth());
            canvas.setHeight(newValue.getHeight());
            gc.drawImage(original, 0, 0);
        });

        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        theStage.setScene(scene);
        theStage.show();
        Font f = new Font(20);

        scoreText.setFont(f);
        scoreText.setX(800);
        scoreText.setY(100);
        scoreText.setText("Score:" + score);
        scoreText.setFill(Color.WHITE);

        ArrayList<String> input = new ArrayList<>();
        scene.setOnKeyPressed(
                new EventHandler<KeyEvent>(){
                    public void handle(KeyEvent e){
                        String code = e.getCode().toString();
                        if(!input.contains(code)){
                            input.add(code);
                        }
                    }
                });

        Image left = new Image(ResourceLoader.square.openStream());
        Image sun = new Image(ResourceLoader.spike.openStream());

        new AnimationTimer(){
            public void handle(long currentNanoTime){
                gc.drawImage(original, 0, 0);
                gc.drawImage(left, x,y);

                if(timeinair >= JUMPTIME){
                    //t = 0.5;
                    if(y < HEIGHT - 128){
                        y += JUMPVALUE;
                        //t += .5;
                    }
                    if(y >= HEIGHT - 128){
                        //t = 0.5;
                        timeinair = 0;
                        moveDown = false;
                        if(input.contains("UP")) input.remove(input.get(0));
                    }
                }
                if(input.contains("UP")){
                    moveDown = false;

                    if(timeinair < JUMPTIME && !moveDown){
                        jump(left, gc);
                        timeinair++;
                    }
                    else if(timeinair >= JUMPTIME && !moveDown){
                        //timeinair = 0;
                        moveDown = true;
                    }
                }
                if((x < 0 || x > WIDTH - 128)) x = -10000;
                if(enemyList.isEmpty()){
                    createEnemy();
                }
                Enemy e = enemyList.get(0);
                e.update(sun, gc);
                if(!e.isAlive) handleEnemyDeath(left, gc);
                if(x - e.x > 64 && !scoreDidTrigger){
                    speedCounter+=0.5;
                    score++;
                    scoreText.setText("Score:" + (score - 1));
                    if(score % 5 == 0 && JUMPTIME != 20) JUMPTIME -= 10;
                    scoreDidTrigger = true;
                }
                if(e.didCollide(x, y)){
                    restart(left, gc, input);
                }

            }
        }.start();
        theStage.show();
    }

    public void start(Stage theStage) throws IOException
    {

        Font f = new Font(20);
        Group root = new Group();
        Scene s = new Scene(root);
        theStage.setScene(s);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        scoreText.setFont(f);
        scoreText.setX(800);
        scoreText.setY(100);
        scoreText.setText("Score:" + score);
        scoreText.setFill(Color.WHITE);
        root.getChildren().add(canvas);
        root.getChildren().add(scoreText);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        //Animates this image, making it move using nanotime
       /* Image space = new Image("download (1).jpeg");
        final long start = System.nanoTime();
        new AnimationTimer(){
            public void handle(long currentNanoTime){
                double t = (currentNanoTime - start)/1000000000.0;
                double x = 232 + 128 * Math.cos(t);
                double y = 232 + 128 * Math.sin(t);
                gc.drawImage(space, x, y);
            }
        }.start();
        theStage.show();
    }*/



        ArrayList<String> input = new ArrayList<>();
        s.setOnKeyPressed(
                new EventHandler<KeyEvent>(){
                    public void handle(KeyEvent e){
                        String code = e.getCode().toString();
                        if(!input.contains(code)){
                            input.add(code);
                        }
                    }
                });

        Image left = new Image(ResourceLoader.square.openStream());
        Image sun = new Image(ResourceLoader.spike.openStream());
        Image space = new Image(ResourceLoader.background.openStream());
        new AnimationTimer(){
            public void handle(long currentNanoTime){
                gc.drawImage(space, 0, 0);
                gc.drawImage(left, x,y);

                if(timeinair >= JUMPTIME){
                    //t = 0.5;
                    if(y < HEIGHT - 128){
                        y += JUMPVALUE;
                        //t += .5;
                    }
                    if(y >= HEIGHT - 128){
                        //t = 0.5;
                        timeinair = 0;
                        moveDown = false;
                        if(input.contains("UP")) input.remove(input.get(0));
                    }
                }
                if(input.contains("UP")){
                    moveDown = false;

                    if(timeinair < JUMPTIME && !moveDown){
                        jump(left, gc);
                        timeinair++;
                    }
                    else if(timeinair >= JUMPTIME && !moveDown){
                        //timeinair = 0;
                        moveDown = true;
                    }
                }
                if((x < 0 || x > WIDTH - 128)) x = -10000;
                if(enemyList.isEmpty()){
                    createEnemy();
                }
                Enemy e = enemyList.get(0);
                e.update(sun, gc);
                if(!e.isAlive) handleEnemyDeath(left, gc);
                if(x - e.x > 64 && !scoreDidTrigger){
                    speedCounter+=0.5;
                    score++;
                    scoreText.setText("Score:" + (score - 1));
                    if(score % 5 == 0 && JUMPTIME != 20) JUMPTIME -= 10;
                    scoreDidTrigger = true;
                }
                if(e.didCollide(x, y)){
                    restart(left, gc, input);
                }

            }
        }.start();
        theStage.show();
    }

    public void jump(Image image, GraphicsContext gc){
        y -= JUMPVALUE;
        gc.drawImage(image, x, y);
    }

    public void handleEnemyDeath(Image image, GraphicsContext gc){
        gc.drawImage(image, x, y);
        enemyList.remove(0);
        scoreDidTrigger = false;
    }

    public void createEnemy(){
        Enemy e = new Enemy(900, HEIGHT - 64, true, speedCounter);
        enemyList.add(e);
        System.out.println(e.speed);
    }

    public void restart(Image image, GraphicsContext gc, ArrayList<String> input){
        x = 250;
        y = HEIGHT - 128;
        input.clear();
        timeinair = 0;
        moveDown = false;
        speedCounter = 3;
        enemyList.remove(0);
        createEnemy();
        score = 0;
        JUMPTIME = 50;
        scoreDidTrigger = false;
        scoreText.setText("Score:" + score);
        gc.drawImage(image, x, y);
    }
}
