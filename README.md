# Bounty Hunter App - Project Documentation
*Author: Gutter-Bacsi Zsombor*  
*Date: [Insert Date]*

## Table of Contents
1. [Project Introduction](#project-introduction)
2. [Overview + UML](#overview--uml)
3. [Individual Section](#individual-section)
    - [Class: BountyHunter and Statistics](#class-bountyhunter-and-statistics)
    - [Adapters](#adapters)
    - [Data Storage: Json files + JsonHelper](#data-storage-json-files--jsonhelper)
    - [Activities](#activities)
        - [Main Activity](#main-activity)
        - [Hire Hunter Activity](#hire-hunter-activity)
        - [Statistics Activity](#statistics-activity)
        - [Charts](#charts)
        - [Home Activity](#home-activity)
        - [Train Activity](#train-activity)
        - [BattleActivityOLD](#battleactivityold)
4. [Network](#network)
    - [Network Manager](#network-manager)
    - [App](#app)
    - [BattleAttack.java + BattleResult.java](#battleattackjava--battleresultjava)
    - [Multiplayer Setup Activity](#multiplayer-setup-activity)
    - [Battle Activity (in use)](#battle-activity-in-use)

## Project Introduction

My project is a Star Wars Bounty Hunters themed simple "trading cards" game-like Android application. The application is designed to manage, train, and simulate LOCAL and ONLINE battles between bounty hunters.  
The app uses a minimalistic style user interface with many activities such as:
- Hiring hunters
- Training hunters
- Displaying statistics
- Online and Local battles

Data is stored and fetched from JSON files, and network functionality is used for multiplayer battles.

## Overview + UML

![Class Diagram](classdiagram.png)

## Individual Section

### Class: BountyHunter and Statistics
`BountyHunter.java` is the most basic object in the application. It stores all the properties of a single hunter, including statistics. Defense against incoming attacks is also calculated here.  
`Statistics.java` contains individual statistics about a hunter that are only required in specific cases, so it's only added to the hunter when needed.

### Adapters
In the application, there are three Adapters for the different CardView items presented in RecyclerView components:
- `BountyHunterAdapter`
- `HireableHunterAdapter`
- `BountyHunterStatisticsAdapter`

They are very similar in functionality but are required to be separate because each CardView has a different layout and shows different values.

### Data Storage: Json files + JsonHelper
To ensure data doesn't get lost and can be saved, the application uses JSON files:
- `bountyhunters.json`
- `mybountyhunters.json`
- `nothiredbountyhunters.json`
- `Statistics.json`

#### Why four instead of one big?
The idea behind the four JSON files is that they represent different types of data, and separating them felt better. Of course, a single JSON file would also work.

`JsonHelper.java` is used for internal data loading, saving, and updating. It contains methods for loading JSON, saving JSON, and updating, as well as phrasing the raw data to return the desired `BountyHunter` or `Statistics` object.

#### Why not Gson?
I didn't use Gson because manually phrasing JSON shows the inner workings visually, which I find useful for understanding. However, for multiplayer functionality, I added methods to quickly convert objects into JSON and back for network communication.

### Activities

#### Main Activity
This is the page that loads when the application is launched. It has three options:
- **View Home**: See already hired hunters and manage them.
- **View Statistics**: View statistical data of hired hunters.
- **Hire New Bounty Hunter**: Hire new bounty hunters.

#### Hire Hunter Activity
This page presents not-hired bounty hunters. Their names and base stats are shown in a CardView, and with the help of radio buttons, we can select a hunter to hire.

Hunters are loaded from `nothiredbountyhunters.json` and, upon hiring, are removed from this file and moved to `mybountyhunter.json`.

#### Statistics Activity
This page displays two types of data loaded from `Statistics.json`:
- **Global Statistics**: Displays how many hunters were hired, the number of local/online battles, and the total number of training sessions.
- **Hunter Statistics**: Displays individual statistics for hired hunters, including which hunter they defeated or were defeated by.

Hunters are loaded from `mybountyhunter.json` and `Statistics.json`.

#### Charts
Charts can be viewed in the Statistics Activity, showing game statistics using AnyChart-Android. There are three pie charts showing battle statistics:
- **Win Chart**: Shows the percentage of wins in online/local battles for each hunter.
- **Lose Chart**: Shows the percentage of losses in online/local battles for each hunter.
- **Battle Chart**: Shows the percentage of participation in online/local battles for each hunter.

#### Home Activity
This page manages hired hunters, loaded from `mybountyhunter.json`.  
Hunters are shown with their picture, name, and stats, including XP. The stats are constantly updated based on winning battles or training.

Check boxes allow the selection of one or multiple hunters to train or send into battle.

#### Train Activity
If a single hunter is selected, we can send it to train. Training options include:
- **Train Melee**: Adds +1 experience and +3 to melee attack and defense stats.
- **Train Range**: Adds +1 experience and +3 to ranged attack and defense stats.
- **Train XP**: Adds +5 experience and +1 to both melee and ranged attack/defense stats.

After each training session, the hunter’s stats are increased and saved, and the number of training sessions is updated in `Statistics.json`.

#### BattleActivityOLD
This version of the Battle Activity was designed before adding multiplayer functionality. It helps explain how the battle system works without multiplayer.  
The battle is turn-based, and the attack algorithm calculates the damage based on the attacker’s stats. When a hunter’s HP goes below zero, they are punished and removed from `mybountyhunter.json`, while the winner is rewarded.

## Network

### Network Manager
`NetworkManager.java` handles multiplayer functionality, including service discovery, socket communication, and data exchange between devices.

### App
The `NetworkManager` is declared at the application level, so different activities can access the same instance.

### BattleAttack.java + BattleResult.java
These classes facilitate communication between devices during battle. They are converted into JSON messages and sent using `NetworkManager.sendMessage()`.

### Multiplayer Setup Activity
In this activity, a player can either host or join a battle. The host device initializes the server, while the client discovers and connects to the host.

### Battle Activity (in use)
This activity handles both local and online battles. The connection is checked using the `isMultiplayer` flag to determine if the battle is local or online.

