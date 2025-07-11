package com.axtonfn.trajectorymod; // Changed from com.yourname.trajectorymod

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrajectoryMod implements ModInitializer {
    // It's good practice to define your MOD_ID here and ensure it matches fabric.mod.json
    public static final String MOD_ID = "trajectorymod"; // Matches the ID in fabric.mod.json
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("TrajectoryMod loaded!"); // Using the logger now

        // You can add other initialization here if needed
    }
}
