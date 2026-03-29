package net.ray.bettertab.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.ray.bettertab.SinglePlayerTab;
import net.ray.bettertab.config.ConfigGetter;

public final class BetterTabFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            BetterTabCommand.register(dispatcher);
        });
        HudElementRegistry.addLast(
                Identifier.withDefaultNamespace("singleplayer_tab"),
                (guiGraphics, deltaTracker) -> SinglePlayerTab.renderTab(guiGraphics)
        );
    }
}
