package net.tricube.better_tab.config;

import net.minecraft.network.chat.Component;
import net.tricube.CraftConfig.api.controller.*;
import net.tricube.CraftConfig.api.registry.CraftConfigRegistry;
import net.tricube.CraftConfig.api.v1.ConfigCategory;
import net.tricube.CraftConfig.api.v1.ConfigOption;
import net.tricube.CraftConfig.api.v1.ConfigSection;
import net.tricube.CraftConfig.api.v1.CraftConfig;
import net.tricube.better_tab.ModInit;

import java.awt.*;

@SuppressWarnings("unchecked")
public class Config {

	public static final ConfigOption<Integer> maxRows =
			ConfigOption.intOption(Component.literal("Maximum Rows"), 20)
					.description(Component.literal("Maximum Rows to display in tab"));

	public static final ConfigOption<Integer> maxCols =
			ConfigOption.intOption(Component.literal("Maximum Columns"), 2)
					.description(Component.literal("Maximum Columns to display in tab"));

	public static final ConfigOption<Boolean> enableIcon =
			ConfigOption.booleanOption(Component.literal("Enable Player Icon"), true)
					.description(Component.literal("Enable default vanilla player skin icon beside names."));

    public static final ConfigOption<Boolean> enableNumericalPing =
            ConfigOption.booleanOption(Component.literal("Enable Numerical Ping"), true)
                    .description(Component.literal("Enable numerical ping and disable the default ping icon"));

    public static final ConfigOption<String> numericalFormat =
            ConfigOption.stringOption(Component.literal("Numerical Ping Format"), "{ping}")
                    .description(Component.literal(	"Formatting for numerical ping, \nsupporting the use of minecraft color codes\n(e.g. &a). Placeholders: {ping}."));

    public static final ConfigOption<Float> scale =
                ConfigOption.floatOption(Component.literal("Ping Text Scale"), 0.7f)
                    .description(Component.literal("Scale for numerical ping"));

    public static final ConfigOption<Boolean> enableInfo =
            ConfigOption.booleanOption(Component.literal("Enable Footer Info"), true)
                    .description(Component.literal("Enable extra footer information below tab."));

    public static final ConfigOption<String> footerInfoFormat =
            ConfigOption.stringOption(Component.literal("Footer Info Format"), "&7TPS: {tps} &7PING: {ping}&7ms")
                    .description(Component.literal("Formatting for extra footer information,\nsupporting the use of minecraft color codes\n(e.g. &a). Placeholders: {ping} {tps} {mspt} {fps}."));

    public static final ConfigOption<Color> above19 =
            ConfigOption.colorOption(Component.literal(">19 TPS"), new Color(0x55FF55))
                    .description(Component.literal(">19 TPS"));

    public static final ConfigOption<Color> above18 =
            ConfigOption.colorOption(Component.literal(">18 TPS"), new Color(0xFFFF55))
                    .description(Component.literal(">18 TPS"));

    public static final ConfigOption<Color> above16 =
            ConfigOption.colorOption(Component.literal(">16 TPS"), new Color(0xFFAA00))
                    .description(Component.literal(">16 TPS"));

    public static final ConfigOption<Color> above10 =
            ConfigOption.colorOption(Component.literal(">10 TPS"), new Color(0xFF5555))
                    .description(Component.literal(">10 TPS"));

    public static final ConfigOption<Color> below10 =
            ConfigOption.colorOption(Component.literal("≤10 TPS"), new Color(0xAA0000))
                    .description(Component.literal("≤10 TPS"));

    public static final ConfigOption<Color> zero =
            ConfigOption.colorOption(Component.literal("N/A"), new Color(0x555555))
                    .description(Component.literal("0ms"));

    public static final ConfigOption<Color> below100 =
            ConfigOption.colorOption(Component.literal("<100ms"), new Color(0x55FF55))
                    .description(Component.literal("<100ms"));

    public static final ConfigOption<Color> below200 =
            ConfigOption.colorOption(Component.literal("<200ms"), new Color(0xFFFF55))
                    .description(Component.literal("<200ms"));

    public static final ConfigOption<Color> below300 =
            ConfigOption.colorOption(Component.literal("<300ms"), new Color(0xFFAA00))
                    .description(Component.literal("<300ms"));

    public static final ConfigOption<Color> below500 =
            ConfigOption.colorOption(Component.literal("<500ms"), new Color(0xFF5555))
                    .description(Component.literal("<500ms"));

    public static final ConfigOption<Color> above500 =
            ConfigOption.colorOption(Component.literal("≥500ms"), new Color(0xAA0000))
                    .description(Component.literal("≥500ms"));


    public static final CraftConfig config = CraftConfig.create(ModInit.MOD_ID)
            .title(Component.literal("Better Tab Config"))
			.category(ConfigCategory.builder(Component.literal("Tab Settings"))
					.section(ConfigSection.builder(Component.literal("Tab"))
							.option(enableIcon.controller(new BooleanController()))
							.option(maxCols.controller(new SliderController<>(0, 10)))
							.option(maxRows.controller(new SliderController<>(0, 100)))

							.build())
					.section(ConfigSection.builder(Component.literal("Ping"))
							.option(enableNumericalPing.controller(new BooleanController()))
							.option(numericalFormat.controller(new InputFieldController<>()))
							.option(scale.controller(new SliderController<>(0.1f, 2.0f)))
							.build())
					.section(ConfigSection.builder(Component.literal("Footer"))
							.option(enableInfo.controller(new BooleanController()))
							.option(footerInfoFormat.controller(new InputFieldController<>()))
							.build())
					.build())
            .category(ConfigCategory.builder(Component.literal("TPS Color"))
                    .section(ConfigSection.builder(Component.literal("TPS Thresholds"))
                            .option(above19.controller(new ColorController()))
                            .option(above18.controller(new ColorController()))
                            .option(above16.controller(new ColorController()))
                            .option(above10.controller(new ColorController()))
                            .option(below10.controller(new ColorController()))
                            .build())
                    .build())

            .category(ConfigCategory.builder(Component.literal("Ping Color"))
                    .section(ConfigSection.builder(Component.literal("Ping Thresholds"))
                            .option(zero.controller(new ColorController()))
                            .option(below100.controller(new ColorController()))
                            .option(below200.controller(new ColorController()))
                            .option(below300.controller(new ColorController()))
                            .option(below500.controller(new ColorController()))
                            .option(above500.controller(new ColorController()))
                            .build())
                    .build())

            .build();


    public static void init() {
        config.load();
        CraftConfigRegistry.register(ModInit.MOD_ID, config, "Better Tab")
                .setModMenuEnabled(true)
                .setCommandEnabled(true)
                .setCustomCommand("bettertab")
                .build();
    }
}
