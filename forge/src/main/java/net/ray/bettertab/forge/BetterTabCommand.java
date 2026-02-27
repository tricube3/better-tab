package net.ray.bettertab.forge;

import com.mojang.brigadier.CommandDispatcher;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ray.bettertab.config.Config;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = "better_tab", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BetterTabCommand {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("bettertab")
                .executes(context -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.level == null) return 0;

                    client.tell(() -> {
                        try {
                            Supplier<Screen> screenSupplier = AutoConfig.getConfigScreen(
                                    Config.class,
                                    client.screen
                            );
                            Screen configScreen = screenSupplier.get();
                            client.setScreen(configScreen);

                        } catch (Exception e) {
                            System.err.println("Failed to open config: " + e);
                            if (client.player != null) {
                                client.player.displayClientMessage(
                                        Component.literal("Error while opening config"),
                                        false
                                );
                            }
                        }
                    });

                    return 1;
                })
        );
    }
}