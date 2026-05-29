package net.tricube.better_tab.mixin;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerTabOverlay.class)
public class IconMixin { //enable player icon rendering in offline mode servers
	//~ if >=26.1 'render' -> 'extractRenderState'
    @ModifyVariable(method = "render", at = @At(value = "STORE"), ordinal = 0)
    private boolean forceBlToTrue(boolean bl) {
        return true;
    }
}
