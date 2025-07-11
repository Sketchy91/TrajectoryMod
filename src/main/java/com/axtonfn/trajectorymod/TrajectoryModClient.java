package com.axtonfn.trajectorymod; // Changed from com.yourname.trajectorymod

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents; // Added for key press handling
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text; // For sending messages to the player
import org.lwjgl.glfw.GLFW;

public class TrajectoryModClient implements ClientModInitializer {
    public static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.trajectorymod.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.misc"
        ));

        // Register a listener for client tick events to handle key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                TrajectoryConfig.toggleEnabled();
                if (client.player != null) {
                    client.player.sendMessage(Text.translatable("key.trajectorymod.toggle.message",
                        TrajectoryConfig.isEnabled() ? Text.literal("enabled").formatted(net.minecraft.util.Formatting.GREEN) : Text.literal("disabled").formatted(net.minecraft.util.Formatting.RED)),
                        false); // false means it's not an action bar message
                }
            }
        });

        TrajectoryRenderer.register();
    }
}
