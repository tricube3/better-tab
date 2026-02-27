package net.ray.bettertab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class SinglePlayerTab {
    public static void renderTab(GuiGraphics guiGraphics){
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer() && mc.player != null) {
            PlayerTabOverlay tabList = mc.gui.getTabList();
            if (tabList != null) {
                boolean tabPressed = mc.options.keyPlayerList.isDown();
                if (tabPressed) {
                    Scoreboard scoreboard = mc.level.getScoreboard();
                    Objective objective = scoreboard.getDisplayObjective(1);
                    int screenWidth = mc.getWindow().getGuiScaledWidth();
                    tabList.setVisible(true);
                    tabList.render(guiGraphics, screenWidth, scoreboard, objective);
                } else {
                    tabList.setVisible(false);
                }
            }
        }
    }
}
