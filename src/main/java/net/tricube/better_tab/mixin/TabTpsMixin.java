package net.tricube.better_tab.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.tricube.better_tab.InfoData;
import net.tricube.better_tab.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ClientPacketListener.class)
public class TabTpsMixin {

    private final float[] tpsSamples = new float[10];
    private int sampleIndex = 0;
    private long lastPacketTime = -1;
    private long gameJoinedTime;
    private float serverMspt = -1;
    private double accumulatedInterval = 0;

    @Inject(method = "handleSetTime", at = @At("HEAD"))
    private void onWorldTimeUpdate(
            ClientboundSetTimePacket packet,
            CallbackInfo ci
    ) {
        long now = System.currentTimeMillis();

        Minecraft mc = Minecraft.getInstance();
        boolean isSingleplayer = mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null;

        if (isSingleplayer) {
            updateFromServer(mc.getSingleplayerServer());
        } else {
			if (System.currentTimeMillis() - gameJoinedTime < 1000) {
				return;
			}
            updateFromPacket(now);
        }
    }

    private void updateFromServer(MinecraftServer server) { //todo: get tps natively instead of calculating via mspt
		//~ if >=1.21 '.getAverageTickTime()' -> '.getCurrentSmoothedTickTime()'
        float mspt = server.getCurrentSmoothedTickTime();
        if (mspt > 0 && mspt < 1000) {
            serverMspt = mspt;
            float tps = 1000.0f / mspt;
			storeTPS(Math.min(tps,20.0f),true);
            InfoData.setCurrentMSPT(mspt);
        }
    }//should implement native way of getting tps

    private void updateFromPacket(long now) {
        if (lastPacketTime == -1) {
            lastPacketTime = now;
            gameJoinedTime = now;
            accumulatedInterval = 0;
            return;
        }

        long interval = now - lastPacketTime;

        if (interval > 100) {
            double totalInterval = interval + accumulatedInterval;
            double tps = 20000.0 / totalInterval;

			tps = Math.max(0, tps);

            tpsSamples[sampleIndex] = (float) tps;
            sampleIndex = (sampleIndex + 1) % tpsSamples.length;

            accumulatedInterval = 0;
        } else {
            accumulatedInterval += interval;
        }

        lastPacketTime = now;
        updateTpsFromSamples();
    }

    private void updateTpsFromSamples() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
			InfoData.setTpsSuffix("?");
            InfoData.setCurrentTPS(0.0f);
            return;
        }

        if (System.currentTimeMillis() - gameJoinedTime < 5000) {
			InfoData.setTpsSuffix("?");
            InfoData.setCurrentTPS(0.0f);
            return;
        }

        int validSamples = 0;
        float sumTps = 0;

        for (float tps : tpsSamples) {
            if (tps > 0) {
                sumTps += tps;
                validSamples++;
            }
        }

        if (validSamples < 5) {
            InfoData.setCurrentTPS(0.0f);
            return;
        }

        float avgTps = sumTps / validSamples;
        float avgMspt = 1000.0f / avgTps;

		storeTPS(avgTps,false);
        InfoData.setCurrentMSPT(avgMspt);
    }

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void onGameJoin(CallbackInfo ci) {
        reset();
    }

    private void reset() {
        serverMspt = -1;
        Arrays.fill(tpsSamples, 0);
        sampleIndex = 0;
        lastPacketTime = -1;
        accumulatedInterval = 0;
        gameJoinedTime = System.currentTimeMillis();
		InfoData.setCurrentTPS(0.0f);
		InfoData.setTpsSuffix("?");
    }

	private void storeTPS(float tps,boolean singleplayer){
		if(tps>20.0f){
			InfoData.setTpsSuffix("*");
		} else{
			InfoData.setTpsSuffix("");
		}
		if(!singleplayer && Config.bypassLimit.get()){
			InfoData.setCurrentTPS(tps);
			InfoData.setTpsSuffix("");
		}else{
			InfoData.setCurrentTPS(Math.min(20.0f, tps));
		}
		if(singleplayer){ //dont account for extra tps since tps is calculated via mspt, resulting in >20 tps
			InfoData.setTpsSuffix("");
		}
	}
}
