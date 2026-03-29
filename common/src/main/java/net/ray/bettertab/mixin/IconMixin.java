package net.ray.bettertab.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(PlayerTabOverlay.class)
public class IconMixin { //enable player icon rendering in offline mode servers

    @ModifyVariable(
            method = "extractRenderState",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            name = "showHead"
    )
    private boolean forceBlToTrue(boolean bl) {
        return true;
    }
}