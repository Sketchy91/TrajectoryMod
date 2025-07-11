package com.axtonfn.trajectorymod.mixin; // Corrected package name

import com.axtonfn.trajectorymod.TrajectoryMod; // Import your mod's main class for the logger
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class) // This annotation tells Mixin which class to target (Minecraft's PlayerEntity)
public abstract class PlayerEntityMixin {

    // This Mixin targets the constructor of PlayerEntity
    // @Inject tells Mixin to inject code
    // method = "<init>" targets the constructor
    // at = @At("TAIL") means the injection happens at the very end of the constructor
    // CallbackInfo ci is used to get information about the method call
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onPlayerEntityInit(CallbackInfo ci) {
        // Log a message when a PlayerEntity is initialized
        TrajectoryMod.LOGGER.info("PlayerEntity initialized via Mixin!");
    }
}
