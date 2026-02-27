package net.ray.bettertab.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.ray.bettertab.TPSvalue;
import net.ray.bettertab.config.Config;
import net.ray.bettertab.config.ConfigGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class TabMixin {

	@Shadow @Final
	private Minecraft minecraft;

	@Shadow @Nullable
	private Component footer;

	@Shadow @Nullable
	private Component header;


//	@Inject(method = "getPlayerInfos", at = @At("HEAD"), cancellable = true)
//	private void onGetPlayerInfos(CallbackInfoReturnable<List<PlayerInfo>> cir) {
//		if (minecraft.hasSingleplayerServer() && minecraft.level != null) {
//			List<PlayerInfo> playerInfos = new ArrayList<>();
//
//			for (AbstractClientPlayer player : minecraft.level.players()) {
//				PlayerInfo info = new PlayerInfo(player.getGameProfile(), false);
//				PlayerInfoAccessor accessor = (PlayerInfoAccessor) info;
//				if (player.isCreative()) {
//					accessor.setGameMode(GameType.CREATIVE);
//				} else if (player.isSpectator()) {
//					accessor.setGameMode(GameType.SPECTATOR);
//				} else {
//					accessor.setGameMode(GameType.SURVIVAL);
//				}
//
//				playerInfos.add(info);
//			}
//
//			cir.setReturnValue(playerInfos);
//		}
//	}

	@Inject(
			method = "setFooter",
			at = @At("TAIL")
	)
	private void modifyFooter(@Nullable Component component, CallbackInfo ci) {

	}
	private MutableComponent originalText = null;
	@Inject(
			method = "render",
			at = @At("HEAD")
	)
	private void updateFooterEveryTick(
			GuiGraphics graphics,
			int width,
			Scoreboard scoreboard,
			@Nullable Objective objective,
			CallbackInfo ci
	) {
		if(!ConfigGetter.config.enableInfo){
			return;
		}
		if(this.footer != null){
			originalText = this.footer.copy();
		}
		else{
			originalText = null;
		}
		if (minecraft.player == null || minecraft.getConnection() == null) return;

		PlayerInfo info =
				minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());

		int ping = info != null ? info.getLatency() : 0;
		double tps = TPSvalue.getCurrentTPS();
		int fps = minecraft.getFps();
		double mspt = TPSvalue.getCurrentMSPT();
		String tpsColor;
		String pingColor;
		if (ping < 100) pingColor = Config.PingColor.below100;
		else if (ping < 200) pingColor = Config.PingColor.below200;
		else if (ping < 300) pingColor = Config.PingColor.below300;
		else if (ping < 500) pingColor = Config.PingColor.below500;
		else {
			ping = Math.min(ping, 999);
			pingColor = Config.PingColor.above500;
		}
		if (tps > 19) tpsColor = Config.TpsColor.above19;
		else if (tps > 18) tpsColor = Config.TpsColor.above18;
		else if (tps > 16) tpsColor = Config.TpsColor.above16;
		else if (tps > 10) tpsColor = Config.TpsColor.above10;
		else tpsColor = Config.TpsColor.below10;
		String text;
		text = ConfigGetter.config.footerInfoFormat
				.replace("{tps}", tpsColor + String.format("%.1f", tps))

				.replace("{mspt}",  String.format("%.1f", mspt*2))
				.replace("{fps}",  String.valueOf(fps));
		if(ping == 0){
			 text = text.replace("{ping}", "&8?");

		}
		else{
			text = text.replace("{ping}", pingColor + String.valueOf(ping));
		}
		text = text.replace("&", "§");

		MutableComponent footerText;
		if(this.footer != null){
			footerText = this.footer.copy();
			footerText.append("\n");
		}

		else{
			footerText = Component.empty();
		}

		this.footer = footerText
				.append(text);
	}
	@Inject(
			method = "render",
			at = @At("TAIL") // inject at the end of render
	)
	private void removeLastFooterLine(
			GuiGraphics graphics,
			  int width,
			  Scoreboard scoreboard,
			  @Nullable Objective objective,
			  CallbackInfo ci){
		this.footer = originalText;
	}
	@Inject(
			method = "renderPingIcon",
			at = @At("HEAD"),
			cancellable = true
	)
	public void renderPingIcon(
			GuiGraphics graphics,
			int width,
			int x,
			int y,
			PlayerInfo player,
			CallbackInfo ci
	) {
		if(!ConfigGetter.config.enableNumericalPing){
			return;
		}
		int ping = player.getLatency();
		String pingColor;
		if (ping < 100) pingColor = Config.PingColor.below100;
		else if (ping < 200) pingColor = Config.PingColor.below200;
		else if (ping < 300) pingColor = Config.PingColor.below300;
		else if (ping < 500) pingColor = Config.PingColor.below500;
		else {
			ping = Math.min(ping, 999);
			pingColor = Config.PingColor.above500;
		}

		float scale = ConfigGetter.config.scale;
		String txt;
		if(ping == 0){
			txt = ConfigGetter.config.numericalFormat.replace("{ping}",
					"&8?").replace("&","§");

		}
		else{
			txt = ConfigGetter.config.numericalFormat.replace("{ping}",
					pingColor + String.valueOf(ping)).replace("&","§");
		}


		FormattedCharSequence text =
				Component.literal(txt).getVisualOrderText();

		int textWidth = minecraft.font.width(text);
		int textHeight = minecraft.font.lineHeight;

		float drawX = (x + width - 1) - (textWidth * scale);
		int slotHeight = 8;
		float drawY = (y + (slotHeight / 2)) - ((textHeight * scale) / 2);

		graphics.pose().pushPose();
		graphics.pose().translate(Math.round(drawX), Math.round(drawY), 100.0F);
		graphics.pose().scale(scale, scale, 1.0F);

		graphics.drawString(
				minecraft.font,
				text,
				0,
				0,
				0x00FFFFFF
		);

		graphics.pose().popPose();
		ci.cancel();
	}
	@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
	private int addPaddingToNameWidth(int k) {
		if (ConfigGetter.config.enableNumericalPing) {
			return k + ConfigGetter.config.offset;
		}
		return k;
	}

	@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 1), ordinal = 2)
	private int addPaddingToScoreWidth(int l) {
		if (ConfigGetter.config.enableNumericalPing) {
			return l + ConfigGetter.config.offset;
		}
		return l;
	}
}
