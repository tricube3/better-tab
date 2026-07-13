package net.tricube.better_tab.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.tricube.better_tab.TabScrollState;
import net.tricube.better_tab.config.Config;
import org.jspecify.annotations.Nullable;
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

	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void updateConfigValues(GuiGraphicsExtractor graphics, int screenWidth, Scoreboard scoreboard, @Nullable Objective displayObjective, CallbackInfo ci) {
		this.visibleCols = Config.maxCols.get();
		this.visibleRows = Config.maxRows.get();
		this.visibleSlots = this.visibleCols * this.visibleRows;
		this.capturedScreenWidth = screenWidth;
	}

	@ModifyConstant(method = "getPlayerInfos", constant = @Constant(longValue = 80L))
	private long maxPlayers(long original) {
		return Integer.MAX_VALUE;
	}

	//? if >=26.2 {
	/*@ModifyVariable(
			method = "extractRenderState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/Minecraft;getConnection()Lnet/minecraft/client/multiplayer/ClientPacketListener;"
			),
			name = "rows"
	)
	private int overrideRows(int rows) {
		return TabScrollState.totalPlayers > this.visibleSlots ? this.visibleRows : rows;
	}

	@ModifyVariable(
			method = "extractRenderState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/Minecraft;getConnection()Lnet/minecraft/client/multiplayer/ClientPacketListener;"
			),
			name = "cols"
	)
	private int overrideCols(int cols) {
		return TabScrollState.totalPlayers > this.visibleSlots ? this.visibleCols : cols;
	}
	*///?} else {
    @ModifyVariable(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;isLocalServer()Z"
            ),
            name = "rows"
    )
    private int overrideRows(int rows) {
        return TabScrollState.totalPlayers > this.visibleSlots ? this.visibleRows : rows;
    }

    @ModifyVariable(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;isLocalServer()Z"
            ),
            name = "cols"
    )
    private int overrideCols(int cols) {
        return TabScrollState.totalPlayers > this.visibleSlots ? this.visibleCols : cols;
    }
    //?}

	@ModifyVariable(
			method = "extractRenderState",
			at = @At(
					value = "STORE",
					ordinal = 0
			),
			name = "maxNameWidth"
	)
	private int capMaxNameWidth(int maxNameWidth) {
		if (TabScrollState.totalPlayers > this.visibleSlots) {
			return Math.min(maxNameWidth, 100);
		}
		return maxNameWidth;
	}

	@ModifyVariable(method = "extractRenderState", at = @At("STORE"), name = "slotWidth")
	private int captureSlotWidth(int slotWidth) {
		this.capturedSlotWidth = slotWidth;
		return slotWidth;
	}

	@ModifyVariable(method = "extractRenderState", at = @At("STORE"), name = "xxo")
	private int captureXxo(int xxo) {
		this.capturedXxo = xxo;
		return xxo;
	}

	@Redirect(
			method = "extractRenderState",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;size()I",
					ordinal = 1
			)
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

	@ModifyArg(
			method = "extractRenderState",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;get(I)Ljava/lang/Object;",
					ordinal = 0
			)
	)
	private int offsetPlayerIndex(int i) {
		return i + TabScrollState.scrollOffset;
	}

	@ModifyArg(
			method = "extractRenderState",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;get(I)Ljava/lang/Object;",
					ordinal = 1
			)
	)
	private int offsetEntryIndex(int i) {
		return i + TabScrollState.scrollOffset;
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void drawScrollbar(
			GuiGraphicsExtractor graphics,
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

	@Redirect(
			method = "extractRenderState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"
			)
	)
	private void extendBackgroundRight(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
		if (color == Integer.MIN_VALUE && this.capturedTotalPlayers > this.visibleSlots) {
			x2 += 5; // extend for scrollbar
		}
		graphics.fill(x1, y1, x2, y2, color);
	}
}
