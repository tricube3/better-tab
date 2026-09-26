package net.tricube.better_tab.mixin;

import net.minecraft.client.multiplayer.PingDebugMonitor;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;
import net.tricube.better_tab.InfoData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PingDebugMonitor.class)
public class PingMixin {

	@Unique
	private long lastPingUpdateTime = -1;
	@Unique
	private final int[] pingSamples = new int[20];
	@Unique
	private int pingSampleIndex = 0;
	@Unique
	private int pingSampleCount = 0;

	@Inject(method = "onPongReceived", at = @At("HEAD"))
	private void onPongReceived(
			ClientboundPongResponsePacket packet,
			CallbackInfo ci
	) {
		int ping = (int) (Util.getMillis() - packet.time());

		pingSamples[pingSampleIndex] = ping;
		pingSampleIndex = (pingSampleIndex + 1) % pingSamples.length;
		if (pingSampleCount < pingSamples.length) pingSampleCount++;
		long now = Util.getMillis();
		if (lastPingUpdateTime == -1 || now - lastPingUpdateTime >= 1000) { //upd every 1s
			lastPingUpdateTime = now;
			InfoData.setCurrentPing(getAveragePing());
		}
	}

	@Unique
	private int getAveragePing() {
		int sum = 0;
		for (int i = 0; i < pingSampleCount; i++) {
			sum += pingSamples[i];
		}
		return pingSampleCount == 0 ? 0 : sum / pingSampleCount;
	}
}
