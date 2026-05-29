package net.tricube.better_tab.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;
import net.tricube.better_tab.ModInit;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		ModInit.onInitialize();
//		FabricEventSubscriber.registerEvents();
	}
}
//?}
