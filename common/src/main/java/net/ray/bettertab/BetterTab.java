package net.ray.bettertab;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.ray.bettertab.config.Config;
import net.ray.bettertab.config.ConfigGetter;

public final class BetterTab {
    public static final String MOD_ID = "better_tab";

    public static void init() {
        AutoConfig.register(Config.class, GsonConfigSerializer::new);
        ConfigGetter.config = AutoConfig.getConfigHolder(Config.class).getConfig();
    }
}
