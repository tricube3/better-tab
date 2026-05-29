package net.tricube.better_tab.mixin;

import net.minecraft.client.Minecraft;
//~ if >=26.1 '.GuiGraphics' -> '.GuiGraphicsExtractor'
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.tricube.better_tab.TPSvalue;
import net.tricube.better_tab.config.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(PlayerTabOverlay.class)
public class TabMixin {

	@Shadow @Final
	private Minecraft minecraft;

	@Shadow @Nullable
	private Component footer;

	@Shadow @Nullable
	private Component header;

	@Inject(
			method = "setFooter",
			at = @At("TAIL")
	)
	private void modifyFooter(@Nullable Component component, CallbackInfo ci) {

	}

	private MutableComponent originalText = null;

	@Inject(
			//~ if >=26.1 'render' -> 'extractRenderState'
			method = "render",
			at = @At("HEAD")
	)
	private void updateFooter(
			//~ if >=26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
			GuiGraphics graphics,
			int width,
			Scoreboard scoreboard,
			@Nullable Objective objective,
			CallbackInfo ci
	) {
		if (!Config.enableInfo.get()) {
			return;
		}
		if (this.footer != null) {
			originalText = this.footer.copy();
		} else {
			originalText = null;
		}
		if (minecraft.player == null || minecraft.getConnection() == null) return;

		PlayerInfo info = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());

		int ping = info != null ? info.getLatency() : 0;
		double tps = TPSvalue.getCurrentTPS();
		int fps = minecraft.getFps();
		double mspt = TPSvalue.getCurrentMSPT();

		Color tpsColor;
		Color pingColor;
		if (ping < 100) pingColor = Config.below100.get();
		else if (ping < 200) pingColor = Config.below200.get();
		else if (ping < 300) pingColor = Config.below300.get();
		else if (ping < 500) pingColor = Config.below500.get();
		else {
			ping = Math.min(ping, 999);
			pingColor = Config.above500.get();
		}

		if (tps > 19) tpsColor = Config.above19.get();
		else if (tps > 18) tpsColor = Config.above18.get();
		else if (tps > 16) tpsColor = Config.above16.get();
		else if (tps > 10) tpsColor = Config.above10.get();
		else tpsColor = Config.below10.get();

		final Color finalPingColor = pingColor;
		final int finalPing = ping;

		MutableComponent tpsComponent = Component.literal(String.format("%.1f", tps))
				.withStyle(s -> s.withColor(tpsColor.getRGB()));

		MutableComponent pingComponent;
		if (ping == 0) {
			pingComponent = Component.literal("?")
					.withStyle(s -> s.withColor(Config.zero.get().getRGB()));
		} else {
			pingComponent = Component.literal(String.valueOf(finalPing))
					.withStyle(s -> s.withColor(finalPingColor.getRGB()));
		}

		MutableComponent msptComponent = Component.literal(String.format("%.1f", mspt * 2));
		MutableComponent fpsComponent = Component.literal(String.valueOf(fps));

		String format = Config.footerInfoFormat.get();
		String[] tokens = format.split("((?=\\{)|(?<=\\}))");

		MutableComponent infoLine = Component.empty();
		for (String token : tokens) {
			switch (token) {
				case "{tps}" -> infoLine.append(tpsComponent);
				case "{ping}" -> infoLine.append(pingComponent);
				case "{mspt}" -> infoLine.append(msptComponent);
				case "{fps}" -> infoLine.append(fpsComponent);
				default -> infoLine.append(Component.literal(token.replace("&", "§")));
			}
		}

		MutableComponent footerText;
		if (this.footer != null) {
			footerText = this.footer.copy();
			footerText.append("\n");
		} else {
			footerText = Component.empty();
		}

		this.footer = footerText.append(infoLine);
	}

	@Inject(
			//~ if >=26.1 'render' -> 'extractRenderState'
			method = "render",
			at = @At("TAIL")
	)
	private void removeLastFooterLine(
			//~ if >=26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
			GuiGraphics graphics,
			int width,
			Scoreboard scoreboard,
			@Nullable Objective objective,
			CallbackInfo ci) {
		this.footer = originalText;
	}

	@Inject(
			//~ if >=26.1 'renderPingIcon' -> 'extractPingIcon'
			method = "renderPingIcon",
			at = @At("HEAD"),
			cancellable = true
	)
	public void renderPingIcon(
			//~ if >=26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
			GuiGraphics graphics,
			int width,
			int x,
			int y,
			PlayerInfo player,
			CallbackInfo ci
	) {
		if (!Config.enableNumericalPing.get()) {
			return;
		}
		int ping = player.getLatency();
		Color pingColor;
		if (ping == 0) pingColor = Config.zero.get();
		else if (ping < 100) pingColor = Config.below100.get();
		else if (ping < 200) pingColor = Config.below200.get();
		else if (ping < 300) pingColor = Config.below300.get();
		else if (ping < 500) pingColor = Config.below500.get();
		else {
			ping = Math.min(ping, 999);
			pingColor = Config.above500.get();
		}

		float scale = Config.scale.get();
		MutableComponent displayComponent;
		String formatString = Config.numericalFormat.get();
		String pingFormat;
		if (ping != 0) {
			pingFormat = String.valueOf(ping);
		} else {
			pingFormat = "?";
		}

		MutableComponent pingComponent = Component.literal(pingFormat)
				.withStyle(style -> style.withColor(pingColor.getRGB()));

		String[] parts = formatString.split("\\{ping\\}", -1);
		displayComponent = Component.empty();

		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].isEmpty()) {
				displayComponent.append(Component.literal(parts[i].replace("&", "§")));
			}
			if (i < parts.length - 1) {
				displayComponent.append(pingComponent);
			}
		}

		FormattedCharSequence text = displayComponent.getVisualOrderText();

		int textWidth = minecraft.font.width(text);
		int textHeight = minecraft.font.lineHeight;

		float drawX = (x + width - 1) - (textWidth * scale);
		int slotHeight = 8;
		float drawY = (y + (slotHeight / 2)) - ((textHeight * scale) / 2);
		//~ if >=1.21.11 'pushPose' -> 'pushMatrix'
		graphics.pose().pushPose();
		//?if>=1.21.11{
		/*graphics.pose().translate(Math.round(drawX), Math.round(drawY));
		graphics.pose().scale(scale, scale);
		*///?}else{
		graphics.pose().translate(Math.round(drawX), Math.round(drawY), 100.0F);
		graphics.pose().scale(scale, scale, 1.0F);
		//?}
		//~ if >=26.1 'drawString' -> 'text'
		graphics.drawString(
				minecraft.font,
				text,
				0,
				0,
				-1
		);
		//~ if >=1.21.11 'popPose' -> 'popMatrix'
		graphics.pose().popPose();
		ci.cancel();
	}

	//~ if >=26.1 'render' -> 'extractRenderState'
	@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
	private int addPaddingToNameWidth(int k) {
		if (Config.enableNumericalPing.get()) {
			return k + Config.offset.get();
		}
		return k;
	}

	//~ if >=26.1 'render' -> 'extractRenderState'
	@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 1), ordinal = 2)
	private int addPaddingToScoreWidth(int l) {
		if (Config.enableNumericalPing.get()) {
			return l + Config.offset.get();
		}
		return l;
	}
}
