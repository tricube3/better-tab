package net.tricube.better_tab.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
//?if < 1.21.11
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//?if >= 1.21.11
//import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.ResourceLocation;
import net.tricube.better_tab.SinglePlayerTab;


@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//?if >= 1.21.11{
		/*HudElementRegistry.addLast(
				ResourceLocation.withDefaultNamespace("singleplayer_tab"),
				(guiGraphics, deltaTracker) -> SinglePlayerTab.renderTab(guiGraphics)
		);
		*///?}else{
		HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
			SinglePlayerTab.renderTab(guiGraphics);
		});
		//?}



	}
}
//?}
