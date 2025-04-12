package com.example.oop_project.util;

public class BattleAttack {
    private String attackerName;
    private String defenderName;
    private int damage;
    private int newHP;
    private boolean isMelee;
    private int tempoDamage;
    private boolean isBattleOver;

    public BattleAttack(String attackerName, String defenderName, int damage, int newHP, boolean isMelee, int tempoDamage, boolean isBattleOver) {
        this.attackerName = attackerName;
        this.defenderName = defenderName;
        this.damage = damage;
        this.newHP = newHP;
        this.isMelee = isMelee;
        this.tempoDamage = tempoDamage;
        this.isBattleOver = isBattleOver;
    }

    public String getAttackerName() {
        return attackerName;
    }

    public void setAttackerName(String attackerName) {
        this.attackerName = attackerName;
    }

    public String getDefenderName() {
        return defenderName;
    }

    public void setDefenderName(String defenderName) {
        this.defenderName = defenderName;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getNewHP() {
        return newHP;
    }

    public void setNewHP(int newHP) {
        this.newHP = newHP;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public void setMelee(boolean melee) {
        isMelee = melee;
    }

    public int getTempoDamage() {
        return tempoDamage;
    }

    public void setTempoDamage(int tempoDamage) {
        this.tempoDamage = tempoDamage;
    }

    public boolean isBattleOver() {
        return isBattleOver;
    }

    public void setBattleOver(boolean battleOver) {
        isBattleOver = battleOver;
    }
}