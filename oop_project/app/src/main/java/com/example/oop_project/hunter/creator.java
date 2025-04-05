package com.example.oop_project.hunter;

public class creator {


    // Factory methods to create specific bounty hunters
    public static BountyHunter createBobaFett() {
        return new BountyHunter(
                "Boba Fett",
                "src/main/res/mipmap-xxxhdpi/boba.jpeg",
                false,  // prefers ranged
                70,     // melee attack
                65,     // melee defense
                90,     // ranged attack
                85,     // ranged defense
                100     // max health
        );
    }

    public static BountyHunter createCadBane() {
        return new BountyHunter(
                "Cad Bane",
                "images/cad_bane.png",
                false,  // prefers ranged
                60,     // melee attack
                60,     // melee defense
                95,     // ranged attack
                80,     // ranged defense
                90      // max health
        );
    }

    public static BountyHunter createAurraSing() {
        return new BountyHunter(
                "Aurra Sing",
                "images/aurra_sing.png",
                false,  // prefers ranged
                65,     // melee attack
                55,     // melee defense
                85,     // ranged attack
                75,     // ranged defense
                85      // max health
        );
    }

    public static BountyHunter createBossk() {
        return new BountyHunter(
                "Bossk",
                "src/main/res/mipmap-xxxhdpi/bosk.jpeg",
                true,   // prefers melee
                85,     // melee attack
                80,     // melee defense
                70,     // ranged attack
                65,     // ranged defense
                120     // max health
        );
    }

    public static BountyHunter createIG88() {
        return new BountyHunter(
                "IG-88",
                "images/ig88.png",
                true,   // prefers melee
                90,     // melee attack
                85,     // melee defense
                75,     // ranged attack
                70,     // ranged defense
                150     // max health (droid)
        );
    }

}