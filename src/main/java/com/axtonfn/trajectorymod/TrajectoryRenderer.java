package com.axtonfn.trajectorymod; // Changed from com.yourname.trajectorymod

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer; // For rendering
import net.minecraft.client.render.VertexConsumer; // For rendering
import net.minecraft.client.render.VertexConsumerProvider; // For rendering
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.client.render.BufferBuilder; // For rendering
import net.minecraft.client.render.Tessellator; // For rendering
import net.minecraft.client.render.VertexFormats; // For rendering
import com.mojang.blaze3d.systems.RenderSystem; // For rendering
import org.joml.Matrix4f; // For rendering

import java.util.Set;
import java.util.HashSet;

public class TrajectoryRenderer {
    private static final Set<Item> THROWABLE_ITEMS = new HashSet<>();

    static {
        THROWABLE_ITEMS.add(Items.BOW);
        THROWABLE_ITEMS.add(Items.CROSSBOW);
        THROWABLE_ITEMS.add(Items.SNOWBALL);
        THROWABLE_ITEMS.add(Items.EGG);
        THROWABLE_ITEMS.add(Items.ENDER_PEARL);
        THROWABLE_ITEMS.add(Items.SPLASH_POTION);
        THROWABLE_ITEMS.add(Items.LINGERING_POTION);
        THROWABLE_ITEMS.add(Items.TRIDENT);
        THROWABLE_ITEMS.add(Items.FISHING_ROD);
        THROWABLE_ITEMS.add(Items.FIRE_CHARGE);
        THROWABLE_ITEMS.add(Items.EXPERIENCE_BOTTLE);
    }

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (!TrajectoryConfig.isEnabled()) return;

            PlayerEntity player = mc.player;
            if (player == null) return;

            ItemStack stack = player.getMainHandStack();
            if (!THROWABLE_ITEMS.contains(stack.getItem())) return;

            MatrixStack matrices = context.matrixStack();
            Vec3d startPos = player.getEyePos();
            // Initial velocity, adjusted for item type if necessary
            // This is a simplified velocity. Real projectile physics in Minecraft
            // can be more complex (e.g., arrow speed, trident speed, potion throw speed).
            // For a basic trajectory, this general approach is a good start.
            Vec3d velocity = player.getRotationVec(1.0f).multiply(getThrowStrength(stack));


            Vec3d current = startPos;
            // Get the camera's position for accurate rendering offset
            Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();

            matrices.push();
            // Translate the rendering origin to the player's position, relative to the camera
            matrices.translate(startPos.x - cameraPos.x, startPos.y - cameraPos.y, startPos.z - cameraPos.z);

            VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines()); // Use RenderLayer.getLines() for line rendering

            Matrix4f modelMatrix = matrices.peek().getPositionMatrix();

            // Draw the starting point
            vertexConsumer.vertex(modelMatrix, 0, 0, 0).color(1.0f, 1.0f, 0.0f, 1.0f).normal(0, 1, 0).next();
            vertexConsumer.vertex(modelMatrix, 0, 0, 0).color(1.0f, 1.0f, 0.0f, 1.0f).normal(0, 1, 0).next(); // Duplicate for a point

            Vec3d prev = Vec3d.ZERO; // Relative to startPos
            for (int i = 0; i < 200; i++) { // Increased iterations for longer trajectory
                Vec3d next = current.add(velocity);

                // Simulate gravity (approximate)
                // Gravity values vary by projectile type in Minecraft.
                // This is a general value for items like snowballs/eggs.
                double gravity = getGravity(stack);
                velocity = velocity.add(0, -gravity, 0); // Apply gravity

                // Simulate air resistance/drag (simple approximation)
                // Projectiles also slow down due to air resistance.
                velocity = velocity.multiply(0.99); // Simple drag factor

                // Raycast to detect collision
                HitResult hit = mc.world.raycast(new RaycastContext(
                    current, next,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, player
                ));

                // Draw segment
                vertexConsumer.vertex(modelMatrix, (float) prev.x, (float) prev.y, (float) prev.z).color(0.0f, 1.0f, 0.0f, 1.0f).normal(0, 1, 0).next();
                vertexConsumer.vertex(modelMatrix, (float) (next.x - startPos.x), (float) (next.y - startPos.y), (float) (next.z - startPos.z)).color(0.0f, 1.0f, 0.0f, 1.0f).normal(0, 1, 0).next();

                if (hit.getType() != HitResult.Type.MISS) {
                    // Draw a red point at the hit location
                    Vec3d hitPos = hit.getPos().subtract(startPos); // Relative to startPos
                    vertexConsumer.vertex(modelMatrix, (float) (hitPos.x), (float) (hitPos.y), (float) (hitPos.z)).color(1.0f, 0.0f, 0.0f, 1.0f).normal(0, 1, 0).next();
                    vertexConsumer.vertex(modelMatrix, (float) (hitPos.x), (float) (hitPos.y), (float) (hitPos.z)).color(1.0f, 0.0f, 0.0f, 1.0f).normal(0, 1, 0).next(); // Duplicate for a point
                    break; // Stop drawing after hit
                }

                current = next;
                prev = next.subtract(startPos); // Update previous relative position
            }

            immediate.draw(); // Flush the buffer
            matrices.pop(); // Restore the matrix stack
        });
    }

    // Helper method to get initial throw strength based on item
    private static float getThrowStrength(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.BOW) {
            // Bow charge can affect velocity, this is a simplified constant
            return 3.0f; // Approx arrow speed
        } else if (item == Items.TRIDENT) {
            return 2.5f; // Approx trident speed
        } else if (item == Items.FISHING_ROD) {
            return 1.5f; // Approx fishing bobber speed
        } else if (item == Items.FIRE_CHARGE) {
            return 0.9f; // Fire charge thrown by player
        } else {
            // Default for snowballs, eggs, potions, pearls, experience bottles
            return 1.5f;
        }
    }

    // Helper method to get gravity based on item
    private static double getGravity(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.BOW || item == Items.TRIDENT) {
            return 0.05; // Arrows and Tridents have less gravity
        } else if (item == Items.FISHING_ROD) {
            return 0.08; // Fishing bobber has slightly more gravity
        } else if (item == Items.FIRE_CHARGE) {
            return 0.02; // Fire charge has very little gravity
        } else {
            // Default for snowballs, eggs, potions, pearls, experience bottles
            return 0.03; // Standard projectile gravity
        }
    }
}
