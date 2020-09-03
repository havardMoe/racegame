import HighScores.HSElement;
import HighScores.HSFunctions;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Controller implements KeyListener, ActionListener, Runnable
{

    private HSFunctions HSF = new HSFunctions();
    View view;

    String currentMap;

    private Thread thread;
    private boolean running = false;

    private BufferStrategy bs;
    private Graphics g;

    private float currentSpeed = 0;
    private CardLayout cardLayout;

    private Boolean twoPlayers = false;
    private Boolean firstPlayer = true;

    private Model player1;
    private Model player2;
    boolean playerTwoIngame = false;
    BufferedImage imageMap;


    public Controller() {
        view = new View(this);

        cardLayout = (CardLayout) view.cards.getLayout();
    }





    private synchronized void start()
    {
        if (running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() //kunne ikke være syncronized
    {
        if(!running) {
            return;
        }

        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void tick(Model player) {
        updateCar(player); //endrer piHundreds variabelen ut i fra hvilke knapper som er holdt inne

        //finner de nye koordinatene ved å plusse på cos/sin*cs
        player.car.trueX += (Math.cos(player.car.piThousands*Math.PI/1000) * player.car.currentSpeed * player.reducedSpeed);
        player.car.trueY += (Math.sin(player.car.piThousands*Math.PI/1000) * player.car.currentSpeed * player.reducedSpeed);

        player.car.PosX = (int) player.car.trueX;
        player.car.PosY = (int) player.car.trueY;
    }


    private void render(boolean twoPlayers) {
        bs = view.getGameCanvas().getBufferStrategy();

        if(bs == null) {
            view.getGameCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics(); //creating the paintbrush
        g.clearRect(0, 0, view.getWidth(), view.getHeight());

        // draw
        g.drawImage(imageMap, 0, 0, null); //tegner bakgrunnen

        if (twoPlayers) {
            player1.car.drawCarImg(g, player1.car.piThousands*Math.PI/1000 - Math.PI/2);
            player2.car.drawCarImg(g, player2.car.piThousands*Math.PI/1000 - Math.PI/2);
        }
        else {
            player1.car.drawCarImg(g, player1.car.piThousands*Math.PI/1000 - Math.PI/2);
        }

        // done drawing
        bs.show();
        g.dispose();
    }

    @Override
    public void run() {

        int fps = 60;
        double timePerTick = 1000000000.0 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long timer = 0;
        int ticks = 0;

        while (running) {
            now = System.nanoTime();
            delta += (now-lastTime) / timePerTick;
            timer += now - lastTime;
            lastTime = now;

            if(delta >= 1) {
                player1.checkWheels();

                tick(player1);

                if(twoPlayers) {
                    tick(player2);
                    player2.checkWheels();
                }

                render(twoPlayers); //rendrer kun player2 dersom twoPlayers er true
                ticks++;
                delta--;
            }

            //FPScounter
            if(timer >= 1000000000){
                System.out.println("FPS:" + ticks);

                timer = 0;
                ticks = 0;
                if (twoPlayers){
                    if (player1.finishedLaps && player2.finishedLaps){
                        stopGame();
                    }
                }
                else if (player1.finishedLaps){
                    stopGame();
                }
            }
        }
        stop();
    }

    private void updateCar(Model player){ //Endrer vinkelen og fart til bien
        if (player.car.getLEFT()) {
            player.car.piThousands -= 20;
        }
        if (player.car.getRIGHT()){
            player.car.piThousands += 20;
        }

        if ((player.car.getUP()) && (currentSpeed < 0.07)){ //max hastighet på 5

            if (player.car.currentSpeed>=5){
                player.car.currentSpeed+=0;
            }
            else
                player.car.currentSpeed += 0.05;
        }
        if((!player.car.getUP())){
            player.car.currentSpeed -= 0.06;
        }

        if ((!player.car.getUP())&&(player.car.currentSpeed<=0)){
            player.car.currentSpeed=0;
        }


        if ((player.car.getDOWN()) &&(currentSpeed > -2)){ //min hastighet på -2
            player.car.currentSpeed -= 0.5;
        }
    }

    public void startGame(){
        cardLayout.show(view.cards, view.GAMEPANEL);

        //Sender inn imageMap så de kan sammenligne pikslene
        player1.imageMap = imageMap;
        player1.finishedLaps = false;
        player1.setStartTime();

        if (twoPlayers){
            player2.imageMap = imageMap;
            player2.finishedLaps = false;
            player2.setStartTime();
        }

        this.start();

        view.getGameCanvas().requestFocus();
    }

    public void stopGame(){

        player1.setFinishTime();
        System.out.println(player1.getScore());
        player1.car.resetCar();

        if(twoPlayers){
            player2.setFinishTime();
            player2.car.resetCar();
        }

        ArrayList<HSElement> playerScores = HSF.getHiScores(currentMap); //henter highscores til en HSElement arrayList
        playerScores = HSF.sortHSE(playerScores); //sorterer slik at høyeste er først

        boolean newHighscore = false;

        //if new highscore, legg til
        if(player1.getScore() < playerScores.get(9).score) {//ny top 10 plasering?
            System.out.println("spiller1 fikk ny rekord");
            playerScores = HSF.setNewHiScore(player1.name, player1.getScore(), playerScores); //legger til nytt HSElement i ALen
            newHighscore = true;
        }

        if(twoPlayers) {
            if (player2.getScore() < playerScores.get(9).score) {
                System.out.println("spiller2 fikk ny rekord");
                playerScores = HSF.setNewHiScore(player2.name, player2.getScore(), playerScores);
                newHighscore = true;
            }
        }

        if(newHighscore) {
            playerScores = HSF.sortHSE(playerScores);
            playerScores = HSF.deleteIfOver10(playerScores); //sletter de med dårligst score, slik at det kun er 10 igjen

            HSF.writeToHiScoreFile(playerScores, currentMap); //skriver nye highscores til txt fila
            newHighscore = false;
        }


        view.playAgainButton.setVisible(true);

        if(currentMap.equals("Map1")){
            view.cbHigscore.setSelectedIndex(0);
        }
        else{
            view.cbHigscore.setSelectedIndex(1);
        }

        HSF.printToTextPanes(HSF.getHiScores(currentMap), view.namesTextPane, view.scoresTextPane);
        cardLayout.show(view.cards, view.HIGHSCOREPANEL);

        this.stop();
    }

    public void playerChoseCar(String carType){
        if (view.nameTextField.getText().isEmpty()|| view.nameTextField.getText().length() > 10) {
            JOptionPane.showMessageDialog(null, "Please write a valid name\nunder 10 characters");
        }

        else{

            if (firstPlayer){
                player1 = new Model(view.nameTextField.getText(), carType);
                firstPlayer = false;
                if (!twoPlayers){
                    cardLayout.show(view.cards, view.CHOOSEMAPPANEL);
                }
                else {
                    view.blankEditPlayer(2);
                }
            }
            else if (twoPlayers){
                player2 = new Model(view.nameTextField.getText(), carType);
                cardLayout.show(view.cards, view.CHOOSEMAPPANEL);
                player2.car.PosX = 150;
                player2.car.PosY = 150;
                player2.car.trueY = 150;
                player2.car.trueX = 150;

            }
            else {
                startGame();
                view.cbHigscore.setEnabled(true);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            player1.car.setLEFT(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            player1.car.setRIGHT(true);
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            player1.car.setUP(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            player1.car.setDOWN(true);
        }

        // Player2 styring
        if (twoPlayers){
            if (e.getKeyCode() == KeyEvent.VK_A) {
                player2.car.setLEFT(true);
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                player2.car.setRIGHT(true);
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
                player2.car.setUP(true);
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                player2.car.setDOWN(true);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //player1
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            player1.car.setLEFT(false);
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            player1.car.setRIGHT(false);
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP){
            player1.car.setUP(false);
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN){
            player1.car.setDOWN(false);
        }

        //player2
        if (twoPlayers){
            if (e.getKeyCode() == KeyEvent.VK_A) {
                player2.car.setLEFT(false);
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                player2.car.setRIGHT(false);
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
                player2.car.setUP(false);
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                player2.car.setDOWN(false);
            }
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JButton){
            JButton button = (JButton) e.getSource();
            String name = button.getName();

            if (name.equals("Main menu")){
                cardLayout.show(view.cards, view.MENUPANEL);
            }
            else if(name.equals("1 Player")){
                view.blankEditPlayer(1);
                firstPlayer = true;
                twoPlayers = false;

                cardLayout.show(view.cards, view.EDITPANEL);
            }
            else if(name.equals("2 Players")){

                view.blankEditPlayer(1);
                cardLayout.show(view.cards, view.EDITPANEL);
                firstPlayer = true;
                twoPlayers = true;
            }
            else if(name.equals("Highscores")){
                view.playAgainButton.setVisible(false);
                String map = (String) view.cbHigscore.getSelectedItem();
                HSF.printToTextPanes(HSF.getHiScores(map), view.namesTextPane, view.scoresTextPane);

                cardLayout.show(view.cards, view.HIGHSCOREPANEL);
            }
            else if(name.equals("grey")){
                playerChoseCar("grey");

            }
            else if(name.equals("yellow")){
                playerChoseCar("yellow");

            }
            else if(name.equals("blue")){

                playerChoseCar("blue");

            }
            else if(name.equals("map1")){
                imageMap = ImageLoader.loadImage("/Maps/Map1.jpg", 1280, 720);
                currentMap = "Map1";

                //Setter posisjonene til bilen/bilene til å være med mållinjen pekende riktig vei i map 1
                player1.car.PosX=600;
                player1.car.PosY=580;
                player1.car.trueX=player1.car.PosX;
                player1.car.trueY=player1.car.PosY;
                player1.car.piThousands=1000;

                if(twoPlayers){
                    player2.car.PosX=600;
                    player2.car.PosY=642;
                    player2.car.trueX=player2.car.PosX;
                    player2.car.trueY=player2.car.PosY;
                    player2.car.piThousands=1000;
                }

                startGame();

            }
            else if(name.equals("map2")){
                imageMap = ImageLoader.loadImage("/Maps/Map2.jpg", 1280, 720);
                currentMap = "Map2";

                //setter posisjonene for map 2
                player1.car.PosX=870;
                player1.car.PosY=582;
                player1.car.trueX=player1.car.PosX;
                player1.car.trueY=player1.car.PosY;
                player1.car.piThousands=1000;

                if(twoPlayers){
                    player2.car.PosX=870;
                    player2.car.PosY=642;
                    player2.car.trueX=player2.car.PosX;
                    player2.car.trueY=player2.car.PosY;
                    player2.car.piThousands=1000;
                }

                startGame();

            }
            else if(name.equals("Play again")){
                cardLayout.show(view.cards, view.CHOOSEMAPPANEL);
            }
        }
        //Oppdaterer highscore til riktig map valgt av brukeren. Kjøres når det er endring i comboBoxen
        else if (view.cbHigscore.equals(e.getSource())){

            String map = (String) view.cbHigscore.getSelectedItem();
            HSF.printToTextPanes(HSF.getHiScores(map), view.namesTextPane, view.scoresTextPane);

        }
    }
}



