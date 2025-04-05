package com.example.oop_project.hunter;
import java.util.ArrayList;
import java.util.List;

public class Statistic {
    // Fields
    private List<Integer> wins;           // Stores IDs of hunters this one beat
    private List<Integer> losts;          // Stores IDs of hunters this one lost to
    private int numberOfTrainingSessions; // Count of training sessions

    // Constructor
    public Statistic() {
        this.wins = new ArrayList<>();
        this.losts = new ArrayList<>();
        this.numberOfTrainingSessions = 0;
    }

    // Methods to get the lists
    public List<Integer> getWins() {
        return new ArrayList<>(wins); // Return copy to preserve encapsulation
    }

    public List<Integer> getLost() {
        return new ArrayList<>(losts); // Return copy to preserve encapsulation
    }

    // Methods to add wins/losses
    public void addWin(int idOfLoser) {
        wins.add(idOfLoser);
    }

    public void addLost(int idOfWinner) {
        losts.add(idOfWinner);
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

    // Optional: Method to get win/loss ratio
    public double getWinLossRatio() {
        if (losts.size() == 0) {
            return wins.size(); // Avoid division by zero
        }
        return (double) wins.size() / losts.size();
    }
}