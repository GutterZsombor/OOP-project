package com.example.oop_project;

import com.example.oop_project.hunter.BountyHunter;

public class Home {
    private Storage storage; //HOME needs to add the hunter to storage

    public Home(Storage storage) {
        this.storage = storage;
    }

    public BountyHunter hireBountyHunter(BountyHunter hunter) {
        // Add any hiring logic here, such as:
        // - Validating hunter data
        // - Assigning initial stats (if not already set)
        // - Displaying a confirmation message

        // For now, we'll simply add the hunter to storage:
        storage.addBountyHunter(hunter);
        return hunter;
    }
}
