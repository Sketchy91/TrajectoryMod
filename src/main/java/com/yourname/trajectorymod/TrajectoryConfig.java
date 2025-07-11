package com.yourname.trajectorymod;

public class TrajectoryConfig {
    private static boolean enabled = true;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggleEnabled() {
        enabled = !enabled;
    }
}
