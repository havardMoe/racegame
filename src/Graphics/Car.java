package Graphics;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Car extends GraphicalElement {
    private BufferedImage image;

    public int piThousands = 0; //vinkel på: (PI * piThousands / 1000) radianer

    public float currentSpeed = 0;
    public int laps = 0;

    private boolean UP = false;
    private boolean DOWN = false;
    private boolean LEFT = false;
    private boolean RIGHT = false;
    public boolean crashFront = false;
    public boolean crashBack = false;

    public float trueX = 100;
    public float trueY = 100;

    public static int width = 30;
    public static int height = 58;

    public Car(BufferedImage CarImage) {
        height=58;
        PosX=100;
        PosY=100;

        image = CarImage;
    }

    public void drawCarImg(Graphics g , double radian){
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        g2d.rotate(radian, PosX+(int)(width/2), PosY+(int)(height/2)); //roterer med gitt vinkel, med midten av bilen som anker
        g2d.drawImage(image, PosX, PosY, width, height, null); //tegner bilen
        g2d.rotate(-radian, PosX+(int)(width/2), PosY+(int)(height/2)); //roterer tilbake slik at neste bil blir tegnet på riktig måte
    }

    private Coordinate getCenter() {
        return new Coordinate((this.PosX + Car.width/2), (this.PosY + Car.height/2));
    }

    private Coordinate rotatePoint(Coordinate center, Coordinate point, double radian) {
        int centerX = center.xCoord;
        int centerY = center.yCoord;

        double tempX = point.xCoord - centerX;
        double tempY = point.yCoord - centerY;

        double rotatedX = tempX*Math.cos(radian) - tempY*Math.sin(radian);
        double rotatedY = tempY*Math.sin(radian) + tempY*Math.cos(radian);

        int x = (int) (rotatedX + centerX);
        int y = (int) (rotatedY + centerY);

        return new Coordinate(x,y);
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setLEFT(Boolean b){
        if (crashFront || crashBack){
            this.LEFT = false;
        }
        else{
            this.LEFT = b;
        }
    }

    public void setRIGHT(Boolean b){
        if (crashFront || crashBack){
            this.RIGHT = false;

        }
        else{
            this.RIGHT = b;
        }
    }

    public void setUP(Boolean b){
        if (crashFront){ //Hvis man krasjer foran kan kan ikke kjøre fremover. Hvis krasj bak, kan kjøre fremover
            //System.out.println("Krasj foran");
        }
        else{
            this.UP = b;
        }
    }

    public void setDOWN(Boolean b){
        if (crashBack){

            //System.out.println("krash bak");
        }
        else {
            this.DOWN = b;
        }
    }

    public Boolean getLEFT(){
        return LEFT;
    }
    public Boolean getRIGHT(){
        return RIGHT;
    }
    public Boolean getUP(){
        return UP;
    }
    public Boolean getDOWN(){
        return DOWN;
    }

    public void resetCar() {
        this.PosY = 100;
        this.PosX = 100;
        this.trueX = 100;
        this.trueY = 100;
        this.RIGHT = false;
        this.LEFT = false;
        this.UP = false;
        this.DOWN = false;
        this.currentSpeed = 0;
        this.piThousands = 0;
        this.crashFront = false;
        this.crashBack = false;
        this.laps = 0;
    }
}
