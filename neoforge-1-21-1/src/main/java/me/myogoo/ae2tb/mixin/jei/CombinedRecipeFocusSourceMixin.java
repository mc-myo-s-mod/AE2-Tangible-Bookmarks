package me.myogoo.ae2tb.mixin.jei;

import me.myogoo.ae2tb.client.KeyBindings;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.UserInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombinedRecipeFocusSource.class)
public class CombinedRecipeFocusSourceMixin {
    @Inject(
            method = "isConflictingVanillaMouseButton(Lmezz/jei/gui/input/UserInput;Lmezz/jei/common/input/IInternalKeyMappings;)Z",
            at = @At(value = "RETURN"),
            cancellable = true,
            require = 1,
            expect = 1,
            remap = false
    )
    private static void ae2tb$isConflictingVanillaMouseButton(UserInput input, IInternalKeyMappings keyBindings, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue()
                || input.is(KeyBindings.PICKUP_SINGLE_ITEM)
                || input.is(KeyBindings.PICKUP_SET_ITEM)
                || input.is(KeyBindings.PICKED_ITEM_AUTOCRAFTING));
    }
}
