package com.yourname.trajectorymod;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

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
            Vec3d velocity = player.getRotationVec(1.0f).multiply(1.5);

            Vec3d current = startPos;
            for (int i = 0; i < 100; i++) {
                current = current.add(velocity);
                velocity = velocity.add(0, -0.05, 0);
                HitResult hit = mc.world.raycast(new RaycastContext(
                    current, current.add(velocity),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, player
                ));
                if (hit.getType() != HitResult.Type.MISS) break;

                // Rendering logic placeholder
            }
        });
    }
}
