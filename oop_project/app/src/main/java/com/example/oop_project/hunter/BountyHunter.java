package com.example.oop_project.hunter;

import com.google.gson.Gson;

import java.io.Serializable;

public class BountyHunter implements Serializable {

    private static int idCounter = 0;


    private String name;
    private String imagePath;
    private boolean preferedAttack;  // true for melee, false for ranged
    private int meleAttack;
    private int meleDefense;
    private int rangedAttack;
    private int rangedDefense;
    private int experience;
    private int health;
    private int maxHealth;
    private int id;
    private Statistic statistic;
    private boolean isSelected;


    public BountyHunter(String name, String imagePath, boolean preferedAttack,
                        int meleAttack, int meleDefense, int rangedAttack,
                        int rangedDefense, int maxHealth,int experience) {
        this.name = name;
        this.imagePath = imagePath;
        this.preferedAttack = preferedAttack;
        this.meleAttack = meleAttack;
        this.meleDefense = meleDefense;
        this.rangedAttack = rangedAttack;
        this.rangedDefense = rangedDefense;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.experience = experience;
        this.id = idCounter++;
        this.statistic = new Statistic();
        this.isSelected = false;
    }

    // damage is caclulated in bounty hunter class
    //it says no useg but it is clearly used in BattleActivity so i don't know why it says no use
    public int meleDefense(BountyHunter attacker) {

        int damage=2;
        int atkdef=attacker.meleAttack() - this.meleDefense;
        int xpdiff=attacker.getExperience() - this.experience;
        damage += (int) Math.ceil((atkdef+(xpdiff)*0.5) *0.75);
        if (damage > 0) {

            if ((this.health-damage) < 0) {
                this.health = 0;
            }
        }
        else {
            damage=3; //min 3 damage
        }
        //System.out.println(damage);
        return damage;
    }

    public int meleAttack() {
        // Return melee attack value or perform attack
        return this.meleAttack;
    }

    public int rangedDefense(BountyHunter attacker) {
        int damage=2;
        int atkdef=attacker.rangedAttack() - this.rangedDefense;
        int xpdiff=attacker.getExperience() - this.experience;
        damage += (int) Math.ceil((atkdef+(xpdiff)*0.5) *0.75);
        if (damage > 0) {
            //this.health -= damage;
            if ((this.health-damage) < 0) {
                this.health = 0;
            }
        } else {
            damage=3;
        }
        //System.out.println(damage);
        return damage;
    }

    public int rangedAttack() {
        // Return ranged attack value or perform attack
        return this.rangedAttack;
    }





    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public boolean isPreferedAttack() { return preferedAttack; } // Added getter
    public void setPreferedAttack(boolean preferedAttack) { this.preferedAttack = preferedAttack; } // Added setter
    public int getMeleAttack() { return meleAttack; }
    public void setMeleAttack(int meleAttack) { this.meleAttack = meleAttack; }
    public int getMeleDefense() { return meleDefense; }
    public void setMeleDefense(int meleDefense) { this.meleDefense = meleDefense; }
    public int getRangedAttack() { return rangedAttack; }
    public void setRangedAttack(int rangedAttack) { this.rangedAttack = rangedAttack; }
    public int getRangedDefense() { return rangedDefense; }
    public void setRangedDefense(int rangedDefense) { this.rangedDefense = rangedDefense; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Statistic getStatistic() { return statistic; }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    //used in Online stuff
    public String toJson() {
        return new Gson().toJson(this);
    }

    public static BountyHunter fromJson(String json) {
        return new Gson().fromJson(json, BountyHunter.class);
    }
}
