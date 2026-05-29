package net.tricube.better_tab.platform.forge;

//? forge {

/*import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tricube.better_tab.ModInit;
import net.tricube.better_tab.SinglePlayerTab;




@Mod.EventBusSubscriber(modid = ModInit.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeEventHandler {

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll(
				"singleplayer_tab",
				(forgeGui, guiGraphics, partialTick, screenWidth, screenHeight) -> SinglePlayerTab.renderTab(guiGraphics)
		);
    }
}
*///?}
