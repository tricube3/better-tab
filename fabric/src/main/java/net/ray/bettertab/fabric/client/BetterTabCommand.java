package net.ray.bettertab.fabric.client;

import com.mojang.brigadier.CommandDispatcher;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.ray.bettertab.config.Config;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class BetterTabCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("bettertab")
                .executes(context -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.level == null) return 0;
                    client.schedule(() -> {
                        try {
                            Supplier<Screen> screenSupplier = AutoConfigClient.getConfigScreen(Config.class, client.screen);
                            Screen configScreen = screenSupplier.get();
                            client.setScreen(configScreen);

                        } catch (Exception e) {
                            System.err.println("Failed to open config: " + e);
                            client.player.sendOverlayMessage(Component.literal("Error while opening config"));
                        }
                    });

                    return 1;
                })
        );
    }
}