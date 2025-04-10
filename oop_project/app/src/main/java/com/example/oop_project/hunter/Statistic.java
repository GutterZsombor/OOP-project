package com.example.oop_project.hunter;
import java.util.ArrayList;
import java.util.List;

public class Statistic {
    // Fields
    private List<String> wins;           // Stores names of hunters this one beat
    private List<String> losts;          // Stores Names of hunters this one lost to
    private int numberOfTrainingSessions; // Count of training sessions

    // Constructor
    public Statistic() {
        this.wins = new ArrayList<>();
        this.losts = new ArrayList<>();
        this.numberOfTrainingSessions = 0;
    }

    // Methods to get the lists
    public List<String> getWins() {
        return new ArrayList<>(wins); // Return copy to preserve encapsulation
    }

    public List<String> getLost() {
        return new ArrayList<>(losts); // Return copy to preserve encapsulation
    }

    // Methods to add wins/losses
    public void addWin(String nameOfLoser) {
        wins.add(nameOfLoser);
    }

    public void addLost(String nameOfWinner) {
        losts.add(nameOfWinner);
    }

    // Counting methods
    public int getNumberOfWins() {
        return wins.size();
    }

    public int getNumberOfLosts() {
        return losts.size();
    }

    public int getNumberOfTrainingSessions() {
        return numberOfTrainingSessions;
    }

    public void addNumberOfTrainingSessions() {
        numberOfTrainingSessions++;
    }


    public double getWinLossRatio() {
        if (losts.size() == 0) {
            return wins.size(); // Avoid division by zero
        }
        return (double) wins.size() / losts.size();
    }
}