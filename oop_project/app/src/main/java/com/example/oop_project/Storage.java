package com.example.oop_project;
import com.example.oop_project.hunter.BountyHunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Storage {
    private String name;
    private Map<Integer, BountyHunter> bountyHunters;

    public Storage(String name) {
        this.name = name;
        this.bountyHunters = new HashMap<>();
    }

    // Add a bounty hunter to storage
    public void addBountyHunter(BountyHunter hunter) {
        if (hunter != null) {
            bountyHunters.put(hunter.getId(), hunter);
            System.out.println("Added " + hunter.getName() + " to " + this.name);
        }
    }

    // Get a bounty hunter by ID
    public BountyHunter getBountyHunter(int id) {
        return bountyHunters.get(id);
    }

    // List all bounty hunters
    public List<BountyHunter> listBountyHunter() {
        return new ArrayList<>(bountyHunters.values());
    }

    // Additional utility methods
    public int count() {
        return bountyHunters.size();
    }

    public boolean contains(int id) {
        return bountyHunters.containsKey(id);
    }

    @Override
    public String toString() {
        return "Storage '" + name + "' contains " + count() + " bounty hunters";
    }
}