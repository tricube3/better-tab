package net.ray.bettertab.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ray.bettertab.SinglePlayerTab;

@Mod.EventBusSubscriber(modid = "better_tab", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabEvent {

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.PLAYER_LIST.id(), "player_list", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
            SinglePlayerTab.renderTab(guiGraphics);
        });
    }
}