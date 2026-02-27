package net.ray.bettertab.forge;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.ray.bettertab.BetterTab;
import net.minecraftforge.fml.common.Mod;
import net.ray.bettertab.config.Config;
import net.ray.bettertab.config.ConfigGetter;

@Mod(BetterTab.MOD_ID)
public final class BetterTabForge {
    public BetterTabForge() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) ->
                                AutoConfig.getConfigScreen(Config.class, parent).get()
                )
        );
        BetterTab.init();
    }
}
