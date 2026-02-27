package net.ray.bettertab.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;


@me.shedaniel.autoconfig.annotation.Config(name = "holo-items")
public class Config implements ConfigData {
    @ConfigEntry.Gui.Tooltip() //Enable numerical ping and disable the default ping icon.
    public boolean enableNumericalPing = true;

    @ConfigEntry.Gui.Tooltip(count = 3)  //Formatting for numerical ping,
    // supporting the use of minecraft color codes (e.g. &a).
    // Placeholders: {ping}.
    public String numericalFormat = "&a{ping}";

    @ConfigEntry.Gui.Tooltip() //Scale for numerical ping
    public float scale = 0.7f;

    @ConfigEntry.Gui.Tooltip(count = 2) //Offset the player name area.
                                        //    Increase this if it is interfering with player names.
    public int offset = 2;


    @ConfigEntry.Gui.Tooltip() //Enable extra footer information below tab.
    public boolean enableInfo = true;

    @ConfigEntry.Gui.Tooltip(count = 3)  //Formatting for extra footer information.
    // supporting the use of minecraft color codes (e.g. &a).
    // Placeholders: {ping} {tps}.
    public String footerInfoFormat = "&7TPS: {tps} &7PING: {ping}ms";

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.CollapsibleObject
    public TpsColor tpsColor = new TpsColor(); //Set colors for {tps} thresholds. Uses minecraft color codes.

    public static class TpsColor {
        public static String above19 = "&a"; //>19
        public static String above18 = "&e"; //>18
        public static String above16 = "&6"; //>16
        public static String above10 = "&c"; //>10
        public static String below10 = "&4"; //<=10
    }

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.CollapsibleObject
    public PingColor pingColor = new PingColor(); //Set colors for {ping} thresholds. Uses minecraft color codes.

    public static class PingColor {
        public static String below100 = "&a"; //<100
        public static String below200 = "&e"; //<200
        public static String below300 = "&6"; //<300
        public static String below500 = "&c"; //<500
        public static String above500 = "&4"; //>=500
    }
}