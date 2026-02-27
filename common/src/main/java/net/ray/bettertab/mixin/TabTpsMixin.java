package net.ray.bettertab.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.ray.bettertab.TPSvalue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ClientPacketListener.class)
public class TabTpsMixin {

    private final float[] tpsSamples = new float[20];
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
            updateFromPacket(now);
        }
    }

    private void updateFromServer(MinecraftServer server) {
        float mspt = server.getAverageTickTime();
        if (mspt > 0 && mspt < 1000) {
            serverMspt = mspt;
            float tps = 1000.0f / mspt;
            TPSvalue.setCurrentTPS(Math.min(20.0f, tps));
            TPSvalue.setCurrentMSPT(mspt);
        }
    }

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

            tps = Math.min(20.0, Math.max(0, tps));

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
            TPSvalue.setCurrentTPS(20.0f);
            return;
        }

        if (System.currentTimeMillis() - gameJoinedTime < 4000) {
            TPSvalue.setCurrentTPS(20.0f);
            return;
        }

        if (serverMspt != -1) {
            float tps = 1000.0f / serverMspt;
            TPSvalue.setCurrentTPS(Math.min(20.0f, tps));
            TPSvalue.setCurrentMSPT(serverMspt);
            return;
        }

        int validSamples = 0;
        float sumTps = 0;

        for (float tps : tpsSamples) {
            if (tps > 0 && tps <= 20) {
                sumTps += tps;
                validSamples++;
            }
        }

        if (validSamples < 5) {
            TPSvalue.setCurrentTPS(20.0f);
            return;
        }

        float avgTps = sumTps / validSamples;
        float avgMspt = 1000.0f / avgTps;

        TPSvalue.setCurrentTPS(avgTps);
        TPSvalue.setCurrentMSPT(avgMspt);
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
    }
}