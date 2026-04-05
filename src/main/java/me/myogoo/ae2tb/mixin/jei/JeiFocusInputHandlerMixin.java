package me.myogoo.ae2tb.mixin.jei;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.client.KeyBindings;
import me.myogoo.ae2tb.integration.ae2.HandleInteraction;
import me.myogoo.ae2tb.mixin.MEStorageMenuStorageMixin;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.FocusInputHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(FocusInputHandler.class)
public class JeiFocusInputHandlerMixin {
    @Final
    @Shadow
    private CombinedRecipeFocusSource focusSource;

    @Inject(method = "handleUserInput", at = @At(value = "INVOKE", target = "Ljava/util/Optional;empty()Ljava/util/Optional;"), cancellable = true, remap = false)
    public void handleUserInput(Screen rawScreen, UserInput input, IInternalKeyMappings keyBindings,
            CallbackInfoReturnable<Optional<IUserInputHandler>> cir) {
        handleMiddleClick(rawScreen, input, keyBindings, KeyBindings.PICKUP_SINGLE_ITEM, InventoryAction.PICKUP_SINGLE);
        handleMiddleClick(rawScreen, input, keyBindings, KeyBindings.PICKUP_SET_ITEM, InventoryAction.SHIFT_CLICK);
        handleMiddleClick(rawScreen, input, keyBindings, KeyBindings.PICKED_ITEM_AUTOCRAFTING,
                InventoryAction.AUTO_CRAFT);
        cir.setReturnValue(Optional.empty());
    }

    @Unique
    private void handleMiddleClick(Screen rawScreen, UserInput input, IInternalKeyMappings keyBindings,
            KeyMapping keyMapping, InventoryAction action) {
        if (!input.is(keyMapping)) {
            return;
        }
        var minecraft = rawScreen.getMinecraft();
        var localPlayer = minecraft.player;

        if (localPlayer != null && localPlayer.containerMenu instanceof MEStorageMenu menu) {
            var menuMixin = ((MEStorageMenuStorageMixin) menu);
            List<IClickableIngredientInternal<?>> ingredientUnderMouse = focusSource
                    .getIngredientUnderMouse(input, keyBindings)
                    .filter(x -> x.getElement().getBookmark().isPresent()).toList();

            for (IClickableIngredientInternal<?> clicked : ingredientUnderMouse) {
                var itemStack = clicked.getElement().getTypedIngredient().getItemStack();
                HandleInteraction.sendPacket(menu, itemStack.get(), action);
            }
        }
    }
}
