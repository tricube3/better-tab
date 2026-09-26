package net.tricube.better_tab;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.tricube.better_tab.config.Config;

import java.awt.*;

public class Placeholder {
	public static MutableComponent formatted(String format, int ping){
		Minecraft minecraft = Minecraft.getInstance();


		double tps = InfoData.getCurrentTPS();
		int fps = minecraft.getFps();
		double mspt = InfoData.getCurrentMSPT();


		String[] tokens = format.split("((?=\\{)|(?<=\\}))");
		MutableComponent infoLine = Component.empty();
		for (String token : tokens) { //TODO: migrate to text placeholder api in future
			switch (token) {
				case "{tps}" -> infoLine.append(formatTPS(tps));
				case "{ping}" -> infoLine.append(formatPing(ping));
				case "{mspt}" -> infoLine.append(formatMSPT(mspt));
				case "{fps}" -> infoLine.append(formatFPS(fps));
				case "{online}" -> infoLine.append(Component.literal(String.valueOf(minecraft.getConnection() != null ? minecraft.getConnection().getListedOnlinePlayers().size() : 0)));
				default -> infoLine.append(Component.literal(token.replace("&", "§")));
			}
		}
		return infoLine;
	}

	public static MutableComponent formatPing(int ping){
		Color pingColor;
		if (ping < 100) pingColor = Config.below100.get();
		else if (ping < 200) pingColor = Config.below200.get();
		else if (ping < 300) pingColor = Config.below300.get();
		else if (ping < 500) pingColor = Config.below500.get();
		else {
			ping = Math.min(ping, 999);
			pingColor = Config.above500.get();
		}
		final Color finalPingColor = pingColor;
		final int finalPing = ping;
		MutableComponent pingComponent;
		if (ping == 0) {
			pingComponent = Component.literal("?")
					.withStyle(s -> s.withColor(Config.zero.get().getRGB()));
		} else {
			pingComponent = Component.literal(String.valueOf(finalPing))
					.withStyle(s -> s.withColor(finalPingColor.getRGB()));
		}
		return pingComponent;
	}
	public static MutableComponent formatTPS(double tps){
		Color tpsColor;
		MutableComponent tpsComponent = Component.empty();

		if (tps > 19) tpsColor = Config.above19.get();
		else if (tps > 18) tpsColor = Config.above18.get();
		else if (tps > 16) tpsColor = Config.above16.get();
		else if (tps > 10) tpsColor = Config.above10.get();
		else tpsColor = Config.below10.get();

		if(tps != 0.0f){
			tpsComponent.append(Component.literal(String.format("%.1f", tps) + InfoData.getTpsSuffix())
					.withStyle(s -> s.withColor(tpsColor.getRGB())));

		}else{
			tpsComponent.append(Component.literal(InfoData.getTpsSuffix())
					.withStyle(s -> s.withColor(Config.na.get().getRGB())));
		}

		return tpsComponent;
	}
	public static MutableComponent formatMSPT(double mspt){
		return Component.literal(String.format("%.1f", mspt * 2));
	}
	public static MutableComponent formatFPS(int fps){
		return  Component.literal(String.valueOf(fps));
	}
}
