package com.example.oop_project.util;

public class BattleResult {
    private String winnerName;
    private String loserName;
    private int finalDamage;
    private int finalHP;
    private boolean isBattleOver;

    public BattleResult(String loserName, String winnerName, int finalDamage, int finalHP, boolean isBattleOver) {
        this.loserName = loserName;
        this.winnerName = winnerName;
        this.finalDamage = finalDamage;
        this.finalHP = finalHP;
        this.isBattleOver = isBattleOver;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public boolean isBattleOver() {
        return isBattleOver;
    }

    public void setBattleOver(boolean battleOver) {
        isBattleOver = battleOver;
    }

    public int getFinalHP() {
        return finalHP;
    }

    public void setFinalHP(int finalHP) {
        this.finalHP = finalHP;
    }

    public int getFinalDamage() {
        return finalDamage;
    }

    public void setFinalDamage(int finalDamage) {
        this.finalDamage = finalDamage;
    }

    public String getLoserName() {
        return loserName;
    }

    public void setLoserName(String loserName) {
        this.loserName = loserName;
    }
}
