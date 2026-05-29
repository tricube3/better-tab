package net.tricube.better_tab.platform.neoforge;

//? neoforge {

/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.tricube.better_tab.ModInit;
import net.tricube.better_tab.SinglePlayerTab;


@EventBusSubscriber(modid = ModInit.MOD_ID)
public class NeoforgeEventHandler {

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiLayersEvent event) {
		event.registerAboveAll(
				ResourceLocation.fromNamespaceAndPath("craftconfig", "singleplayer_tab"),
				(guiGraphics, deltaTracker) -> SinglePlayerTab.renderTab(guiGraphics)
		);
	}
}
*///?}
