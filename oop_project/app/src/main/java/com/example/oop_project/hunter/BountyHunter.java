package com.example.oop_project.hunter;

import java.io.Serializable;

public class BountyHunter implements Serializable {
    // Static field to track number of created bounty hunters
    private static int idCounter = 0;

    // Instance fields
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

    // Constructor
    public BountyHunter(String name, String imagePath, boolean preferedAttack,
                        int meleAttack, int meleDefense, int rangedAttack,
                        int rangedDefense, int maxHealth) {
        this.name = name;
        this.imagePath = imagePath;
        this.preferedAttack = preferedAttack;
        this.meleAttack = meleAttack;
        this.meleDefense = meleDefense;
        this.rangedAttack = rangedAttack;
        this.rangedDefense = rangedDefense;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.experience = 0;
        this.id = idCounter++;
        this.statistic = new Statistic();
        this.isSelected = false;
    }

    // Methods
    public int meleDefense(BountyHunter attacker) {

        int damage=2;
        int atkdef=attacker.meleAttack() - this.meleDefense;
        int xpdiff=attacker.getExperience() - this.experience;
        damage += (int) Math.ceil((atkdef+xpdiff) *0.75);
        if (damage > 0) {
            this.health -= damage;
            if (this.health < 0) {
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
        damage += (int) Math.ceil((atkdef+xpdiff) *0.75);
        if (damage > 0) {
            this.health -= damage;
            if (this.health < 0) {
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

    public static int getNumberOfCreatedBountyHunters() {
        return idCounter;
    }

    // Assuming Statistic is an inner class or imported


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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
