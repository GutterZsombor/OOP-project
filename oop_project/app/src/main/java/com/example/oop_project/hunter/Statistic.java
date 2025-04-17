package com.example.oop_project.hunter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Statistic implements Serializable {

    private final List<String> wins;           // Stores names of hunters this one beat
    private final List<String> losts;          // Stores Names of hunters this one lost to
    private int numberOfTrainingSessions;


    public Statistic() {
        this.wins = new ArrayList<>();
        this.losts = new ArrayList<>();
        this.numberOfTrainingSessions = 0;
    }


    public List<String> getWins() {
        return new ArrayList<>(wins);
    }

    public List<String> getLost() {
        return new ArrayList<>(losts);
    }


    public void addWin(String nameOfLoser) {
        wins.add(nameOfLoser);

        /*for (int i = 0; i < wins.size(); i++) {
            System.out.println(wins.get(i));
        }*/
    }

    public void addLost(String nameOfWinner) {
        losts.add(nameOfWinner);
    }

    // Counting methods
    public int getNumberOfWins() {
        if (wins.size() == 0) {return 0;}return wins.size();
    }

    public int getNumberOfLosts() {
        if (losts.size() == 0) {return 0;}return losts.size();
    }

    public int getNumberOfTrainingSessions() {
        return numberOfTrainingSessions;
    }

    public void addNumberOfTrainingSessions() {
        numberOfTrainingSessions++;
    }


    public double getWinLossRatio() {
        if (losts.size() == 0||wins.size()==0) {
            return wins.size();
        }
        double number = (double) wins.size() / losts.size();
        double rounded = Math.round(number * 100.0) / 100.0;//round two 2 decimal
        return rounded ;
    }
}