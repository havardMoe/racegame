package HighScores;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/*
//Eksempel på bruk av Hiscores
        ArrayList<HSElement> playerScores = HSF.getHiScores(); //henter highscores til en HSE arrayList

        playerScores = HSF.setNewHiScore("aron", 888, playerScores); //legger til et nytt HighScores.HSElement

        playerScores = HSF.sortHSE(playerScores); //sorterer slik at høyeste er først

        playerScores = HSF.deleteIfOver10(playerScores); //sletter de med dårligst score, slik at det kun er 10 igjen

        System.out.println(playerScores.get(0).name + " " + playerScores.get(0).score); //førsteplass
        System.out.println(playerScores.get(9).name + " " + playerScores.get(9).score); //sisteplass

        HSF.writeToHiScoreFile(playerScores); //skriver til txt fila
*/
public class HSFunctions {
    public ArrayList<HSElement> HSEarrayList;

    public ArrayList<HSElement> getHiScores(String map) {
        String path;
        if (map.equals("Map1")){
            path = "Assets/Highscores/scoresMap1.txt";
        }
        else {
            path = "Assets/Highscores/scoresMap2.txt";
        }
        //System.out.println("GetHiScores Function");
        HSEarrayList = new ArrayList<>();

        try
        {
            StringBuilder sb = new StringBuilder();

            File file = new File(path);
            Scanner scanner = new Scanner(file);

            while(scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
                sb.append("\n");
            }
            scanner.close();
            ArrayList<String> nameAndScores = new ArrayList<>(Arrays.asList(sb.toString().split("[\n ]")));
            int p = 0;

            for(int i=0; i<nameAndScores.size(); i+=2)
            {
                HSEarrayList.add(new HSElement("", 0));
                HSEarrayList.get(p).name = nameAndScores.get(i);
                HSEarrayList.get(p).score = Integer.parseInt(nameAndScores.get(i+1));
                p++;
            }
        }
        catch (Exception e) {
            System.out.println("Somthing went wrong reading from file");
            return null;
        }

        return HSEarrayList;
    }


    public ArrayList setNewHiScore(String playerName, int score, ArrayList<HSElement> hiScores) {
        //Fjerner newline og mellomrom i navnet
        String newPlayerName = playerName.replaceAll("[\n ]", "");

        hiScores.add(new HSElement(newPlayerName, score));
        return hiScores;
    }


    public void writeToHiScoreFile(ArrayList<HSElement> hiscores, String map) {
        String path;
        if (map.equals("Map1")){
            path = "Assets/Highscores/scoresMap1.txt";
        }
        else {
            path = "Assets/Highscores/scoresMap2.txt";
        }
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));

            for(int i = 0; i < hiscores.size(); i++)
            {
                writer.append(hiscores.get(i).name);
                writer.append(" ");
                writer.append(Integer.toString(hiscores.get(i).score));


                if (i != 9)
                {
                    writer.append("\n");
                }
            }
            writer.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void printToTextPanes(ArrayList<HSElement> hiscores, JTextPane namePane, JTextPane scorePane) {
        String hsNames;
        String hsScores;
        StringBuilder sbNames = new StringBuilder();
        StringBuilder sbScores = new StringBuilder();

        for(int i = 0; i < hiscores.size(); i++) {
            sbNames.append(i+1 + ". ");
            sbNames.append(hiscores.get(i).name);

            sbScores.append(scoreToString(hiscores.get(i).score));

            if (i != 9) {
                sbNames.append("\n");
                sbScores.append("\n");
            }
        }
        hsNames = sbNames.toString();
        hsScores = sbScores.toString();

        namePane.setText(hsNames);
        scorePane.setText(hsScores);
    }

    public ArrayList deleteIfOver10(ArrayList<HSElement> hiScores) {
        while (hiScores.size() > 10)
            hiScores.remove(hiScores.size()-1);

        return hiScores;
    }

    public ArrayList<HSElement> sortHSE(ArrayList<HSElement> hiScores){//descending
        String tempName;
        int tempScore;

        for(int i=0; i < hiScores.size(); i++) {
            for(int j=i+1; j < hiScores.size(); j++) {
                if (hiScores.get(i).score > hiScores.get(j).score) {
                    tempName = hiScores.get(i).name;
                    tempScore = hiScores.get(i).score;

                    hiScores.get(i).name = hiScores.get(j).name;
                    hiScores.get(i).score = hiScores.get(j).score;

                    hiScores.get(j).name = tempName;
                    hiScores.get(j).score = tempScore;
                }
            }
        }
        return hiScores;
    }

    public static int antallMin(int score) {
        int sek =  score%60;
        int min = (score - sek)/60;
        return min;
    }

    public static String antallSek(int score) {
        int sek =  score%60;
        String sekunder;

        if(sek < 10){
            sekunder = "0" + sek;
        }
        else {
            sekunder = Integer.toString(sek);
        }
        return  sekunder;
    }

    public static String scoreToString(int score) {
        StringBuilder sb = new StringBuilder();
        sb.append(antallMin(score));
        sb.append(":");
        sb.append(antallSek(score));

        return sb.toString();
    }
}
