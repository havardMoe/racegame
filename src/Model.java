import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Graphics.Car;

public class Model {

    String name;
    Car car;
    BufferedImage imageMap;
    float reducedSpeed = 1;

    private long startTime;
    private long lastLapTime;
    private int score;

    public Boolean finishedLaps = false;

    public Model(String playerName, String type) {
        this.name = playerName;
        this.car = new Car(carTypeToImage(type));
}

    private BufferedImage carTypeToImage(String carType){

        String path;

        if (carType.equals("grey")){
            path = "/Cars/carImgGreySmall.png";
        }
        else if (carType.equals("yellow")){
            path = "/Cars/carImgYellowSmall.png";
        }
        else{
            path = "/Cars/carImgBlueSmall.png";
        }

        return ImageLoader.loadImage(path, 30, 58);
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
        lastLapTime = startTime;
    }

    public void setFinishTime() {
        this.score = (int) ((System.currentTimeMillis() - this.startTime) / 1000.0);
    }

    private void newLap(){

        //Hvis man har brukt mindre enn 8 sek rundt banen vil det ikke telles som ny runde.
        //Må sjekkes for å unngå at man får flere laps ved å kjøre sakte over målstreken
        if (((System.currentTimeMillis() - lastLapTime)/1000) > 8){
            car.laps++;
            lastLapTime = System.currentTimeMillis();
            System.out.println(car.laps);
        }
        if (car.laps == 3){
            //Stopper farten
            car.currentSpeed = 0;
            finishedLaps = true;
            this.setFinishTime();
        }
    }

    public void resetScore() {
        this.score = 0;
    }

    public int getScore(){
        return this.score;
    }

    //Finner fargen under hvert av de fire hjulene.
    public void checkWheels(){

        reducedSpeed = 1;

        ArrayList<Integer> wheels = new ArrayList<>();
        //TODO: hjulene sjekker bare en firkant som står rett når man kjører, denne må også roteres.
        //se getCenter og rotatePoint i Car classen

        //Foran, venstre
        wheels.add(imageMap.getRGB(car.PosX, car.PosY));

        //Foran, høyre
        wheels.add(imageMap.getRGB(car.PosX + car.getWidth(), car.getHeight()));

        //Bak, venstre
        wheels.add(imageMap.getRGB(car.PosX, car.PosY + car.getHeight()));

        //Bak. høyre
        wheels.add(imageMap.getRGB(car.PosX + car.getWidth(), car.PosY + car.getHeight()));

        float reduceByFloat = 0;

        //Finner ut hvilken farge det er og om det vil minke farten på bilen. Denne ganges med farten på bilen i controller.
        for (int i = 0; i < 3; i++){
            int color = wheels.get(i);

            if(color == -7748789){ //Gress
                reducedSpeed -= 0.35;
            }
            else if (color == -15111690){ //Vann
                reducedSpeed -= 0.47;
            }
            else if (color == -4017537){ //Sand
                reducedSpeed -= 0.4;
            }

            else if (color == -16777216){ //Svart, men ikke lik farge som på målstreken

                if (!this.car.crashFront && !this.car.crashBack){
                    this.car.currentSpeed = 0;

                    if (i == 0 || i == 1){
                        this.car.crashFront = true;
                        System.out.println("krasj foran");
                    }
                    else{
                        this.car.crashBack = true;
                        System.out.println("krasj bak");
                    }
                }
            }
            else if (color == -395272 || color == -13949395){
                newLap();
            }
            else{
                if (i == 0 || i == 1){
                    this.car.crashFront = false;
                }
                else{
                    this.car.crashBack = false;
                }
            }
        }
        reducedSpeed -= reduceByFloat;
    }
}
