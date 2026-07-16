package net.tricube.better_tab.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.Mth;
import net.tricube.better_tab.TabScrollState;
import net.tricube.better_tab.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class ScrollInputMixin {

	@Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
	private void onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.options.keyPlayerList.isDown()) {

			int total = TabScrollState.totalPlayers;
			int cols = Config.maxCols.get();
			int rows = Config.maxRows.get();
			int visibleSlots = cols * rows;

			int maxOffset = total <= visibleSlots
					? 0
					: ((total - 1) / visibleSlots) * visibleSlots;
			if (total <= visibleSlots) return;
			int step = yOffset > 0 ? -visibleSlots : visibleSlots;

			TabScrollState.scrollOffset = Mth.clamp(
					TabScrollState.scrollOffset + step,
					0,
					maxOffset
			);

			ci.cancel();
		}
	}
}
