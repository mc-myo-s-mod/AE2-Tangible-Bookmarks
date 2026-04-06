package me.myogoo.ae2tb.mixin.jei;

import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.UserInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombinedRecipeFocusSource.class)
public class JeiCombinedRecipeFocusSourceMixin {
    @Inject(method = "isConflictingVanillaMouseButton", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void ae2tb$isConflictingVanillaMouseButton(UserInput input, IInternalKeyMappings keyBindings,
            CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || input.getKey().getName().equals("key.mouse.middle"));
    }
}
