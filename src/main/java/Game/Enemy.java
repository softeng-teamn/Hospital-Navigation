package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy {

    public int x;
    int y;
    public boolean isAlive;
    public double speed;

    public Enemy(int x, int y, boolean isAlive, double speed){
        this.x = x;
        this.y = y;
        this.isAlive = isAlive;
        this.speed = speed;
    }

    public void update(Image image, GraphicsContext gc){
        if(isAlive){
            x -= speed;
            gc.drawImage(image, x, y);
            if(didCollide(0, 700-64)) isAlive = false;
        }
    }

    public boolean didCollide(int otherX, int otherY){
        if(Math.abs(otherX - x) <= 64 && Math.abs(otherY - y) <= 64){
            return true;
        }
        return false;
    }

}
