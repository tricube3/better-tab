package net.tricube.better_tab.mixin;

import net.minecraft.client.Minecraft;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
 //?}
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.tricube.better_tab.TabScrollState;
import net.tricube.better_tab.config.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerTabOverlay.class)
public class ScrollableTabMixin {

	@Unique
	private int visibleCols;
	@Unique
	private int visibleRows;
	@Unique
	private int visibleSlots;

	@Unique
	private int capturedTotalPlayers;
	@Unique
	private int capturedSlotWidth;
	@Unique
	private int capturedXxo;
	@Unique
	private int capturedScreenWidth;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Nullable
	private Component header;
	//~ if >=26.1 'render' -> 'extractRenderState'
	@ModifyConstant(method = "render", constant = @Constant(intValue = 20))
	private int maxRowsPerCol(int original) {
		return this.visibleRows;
	}

	@ModifyConstant(method = "getPlayerInfos", constant = @Constant(longValue = 80L))
	private long maxPlayers(long original) {
		return Integer.MAX_VALUE;
	}


	//~ if >=26.1 'render' -> 'extractRenderState'
	@Inject(method = "render", at = @At("HEAD"))
	//~ if >=26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
	private void updateConfigValues(GuiGraphics graphics, int screenWidth, Scoreboard scoreboard, @Nullable Objective displayObjective, CallbackInfo ci) {
		this.visibleCols = Math.max(Config.maxCols.get(), 1);
		this.visibleRows = Math.max(Config.maxRows.get(), 1);
		this.visibleSlots = this.visibleCols * this.visibleRows;
		var connection = this.minecraft.getConnection();
		TabScrollState.totalPlayers = connection != null ? connection.getListedOnlinePlayers().size() : 0;

	}


	//? if >=26.2 {
	/*@ModifyVariable(method = "extractRenderState",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Minecraft;getConnection()Lnet/minecraft/client/multiplayer/ClientPacketListener;"),name = "rows")
	*///?} else if >=26.1 {
	/*@ModifyVariable(method = "extractRenderState",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Minecraft;isLocalServer()Z"),name = "rows")
	*///?}else{
	@ModifyVariable(method="render", at= @At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;isLocalServer()Z"),
			//~ if >=1.21 '9' -> '11'
			index=11)
	//?}
	private int overrideRows(int rows) {
		return TabScrollState.totalPlayers > this.visibleSlots ? this.visibleRows : rows;
	}
	//? if >=26.2 {
	/*@ModifyVariable(method = "extractRenderState",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Minecraft;getConnection()Lnet/minecraft/client/multiplayer/ClientPacketListener;"),name = "cols")
	*///?} else if >=26.1 {
	/*@ModifyVariable(method = "extractRenderState",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Minecraft;isLocalServer()Z"),name = "cols")
	*///?} else {
	@ModifyVariable(method="render", at=@At( value="INVOKE", target="Lnet/minecraft/client/Minecraft;isLocalServer()Z"),
			//~ if >=1.21 '10' -> '12'
			index=12)//?}
	private int overrideCols(int cols) {
		return TabScrollState.totalPlayers > this.visibleSlots ? this.visibleCols : cols;
	}

	//? if >=26.1 {
	/*@ModifyVariable(method = "extractRenderState", at = @At("STORE"), name = "slotWidth")
*///?} else {
	@ModifyVariable(method = "render", at = @At("STORE"),
			//~ if >=1.21 '13' -> '15'
			index = 15 )
//?}
	private int captureSlotWidth(int slotWidth) {
		this.capturedSlotWidth = slotWidth;
		return slotWidth;
	}

	//? if >=26.1 {
	/*@ModifyVariable(method = "extractRenderState", at = @At("STORE"), name = "xxo")
*///?} else {
	@ModifyVariable(method = "render", at = @At("STORE"),
			//~ if >=1.21 '14' -> '16'
			index = 16
	)
//?}
	private int captureXxo(int xxo) {
		this.capturedXxo = xxo;
		return xxo;
	}


	@Redirect(
			//~ if >=26.1 'render' -> 'extractRenderState'
			method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I",
			//~ if >=1.21 '0' -> '1'
			ordinal = 1)
	)
	private int captureAndCapSlots(List<?> list) {
		int total = list.size();
		this.capturedTotalPlayers = total;
		TabScrollState.totalPlayers = total;

		int maxOffset = total <= this.visibleSlots ? 0 : ((total - 1) / this.visibleSlots) * this.visibleSlots;
		if (TabScrollState.scrollOffset > maxOffset) {
			TabScrollState.scrollOffset = maxOffset;
		}

		return Math.min(total - TabScrollState.scrollOffset, this.visibleSlots);
	}

	//? if >=1.21 {
	//~ if >=26.1 'render' -> 'extractRenderState'
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0))

	private int offsetPlayerIndex(int i) {
		return i + TabScrollState.scrollOffset;
	}
	//?}
	@ModifyArg(
			//~ if >=26.1 'render' -> 'extractRenderState'
			method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;",
			//~ if >=1.21 '0' -> '1'
			ordinal = 1)
	)
	private int offsetEntryIndex(int i) {
		return i + TabScrollState.scrollOffset;
	}


	//~ if >=26.1 'render' -> 'extractRenderState'
	@Inject(method = "render", at = @At("TAIL"))
	private void drawScrollbar(
			//~ if >=26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
			GuiGraphics graphics,
			int screenWidth,
			Scoreboard scoreboard,
			@Nullable Objective displayObjective,
			CallbackInfo ci
	) {
		if (this.capturedTotalPlayers <= this.visibleSlots) {
			return;
		}

		int scrollbarYyo = 10;

		if (this.header != null) {
			List<FormattedCharSequence> headerLines = this.minecraft.font.split(this.header, screenWidth - 50);
			scrollbarYyo += headerLines.size() * 9 + 1;
		}

		int trackHeight = this.visibleRows * 9;
		int trackX = this.capturedXxo + this.visibleCols * this.capturedSlotWidth + (this.visibleCols - 1) * 5 + 2;
		int trackY = scrollbarYyo;

		graphics.fill(trackX, trackY, trackX + 2, trackY + trackHeight, 0x40FFFFFF);

		int totalPages = (this.capturedTotalPlayers + this.visibleSlots - 1) / this.visibleSlots;
		int currentPage = TabScrollState.scrollOffset / this.visibleSlots;

		int thumbHeight = Math.max(4, trackHeight / totalPages);

		int thumbY = trackY
				+ (totalPages <= 1
				? 0
				: Math.round((float) (trackHeight - thumbHeight) * currentPage / (totalPages - 1)));

		graphics.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xB0FFFFFF);
	}

	//? if >=26.1 {
	/*@Redirect(method = "extractRenderState",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
	private void extendBackgroundRight(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
		*///?} else {
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
	private void extendBackgroundRight(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
	//?}
		if (color == Integer.MIN_VALUE && this.capturedTotalPlayers > this.visibleSlots) {
			x2 += 5;
		}
		graphics.fill(x1, y1, x2, y2, color);
	}
}
