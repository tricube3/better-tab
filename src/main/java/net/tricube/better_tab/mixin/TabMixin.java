package net.tricube.better_tab.mixin;

import net.minecraft.client.Minecraft;
//~ if >=26.1 '.GuiGraphics' -> '.GuiGraphicsExtractor'
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.tricube.better_tab.InfoData;import net.tricube.better_tab.Placeholder;
import net.tricube.better_tab.config.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection != null) {connection.send(new ServerboundPingRequestPacket(Util.getMillis()));}
		String format = Config.footerInfoFormat.get();
		MutableComponent infoLine = Placeholder.formatted(format, InfoData.getCurrentPing());
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
		float scale = Config.scale.get();
		int ping = player.getLatency();
		String format = Config.numericalFormat.get();
		MutableComponent displayComponent = Placeholder.formatted(format,ping);
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

//	//~ if >=26.1 'render' -> 'extractRenderState'
//	@ModifyVariable(method = "extractRenderState", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
//	private int addPaddingToNameWidth(int k) {
//		if (Config.enableNumericalPing.get()) {
//			return k + Config.offset.get();
//		}
//		return k;
//	}
//
//	//~ if >=26.1 'render' -> 'extractRenderState'
//	@ModifyVariable(method = "extractRenderState", at = @At(value = "STORE", ordinal = 1), ordinal = 2)
//	private int addPaddingToScoreWidth(int l) {
//		if (Config.enableNumericalPing.get()) {
//			return l + Config.offset.get();
//		}
//		return l;
//	}
}
