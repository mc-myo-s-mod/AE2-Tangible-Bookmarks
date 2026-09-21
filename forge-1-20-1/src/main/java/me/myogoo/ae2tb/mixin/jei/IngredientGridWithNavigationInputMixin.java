package me.myogoo.ae2tb.mixin.jei;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.client.KeyBindings;
import me.myogoo.ae2tb.integration.ae2.HandleInteraction;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.SameElementInputHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigation$UserInputHandler", remap = false)
public class IngredientGridWithNavigationInputMixin {
    @Final
    @Shadow
    private IRecipeFocusSource focusSource;

    @Inject(method = "handleUserInput", at = @At("HEAD"), cancellable = true)
    private void ae2tb$handleBookmarkGridInput(Screen rawScreen, UserInput input, IInternalKeyMappings keyBindings,
            CallbackInfoReturnable<Optional<IUserInputHandler>> cir) {
        ae2tb$handleBookmarkGridAction(rawScreen, input, KeyBindings.PICKED_ITEM_AUTOCRAFTING, InventoryAction.AUTO_CRAFT)
                .or(() -> ae2tb$handleBookmarkGridAction(rawScreen, input, KeyBindings.PICKUP_SET_ITEM,
                        InventoryAction.SHIFT_CLICK))
                .or(() -> ae2tb$handleBookmarkGridAction(rawScreen, input, KeyBindings.PICKUP_SINGLE_ITEM,
                        InventoryAction.PICKUP_SINGLE))
                .ifPresent(handler -> cir.setReturnValue(Optional.of(handler)));
    }

    @Unique
    private Optional<IUserInputHandler> ae2tb$handleBookmarkGridAction(Screen rawScreen, UserInput input,
            KeyMapping keyMapping, InventoryAction action) {
        if (!input.is(keyMapping)) {
            return Optional.empty();
        }

        var localPlayer = rawScreen.getMinecraft().player;
        if (localPlayer == null || !(localPlayer.containerMenu instanceof MEStorageMenu menu)) {
            return Optional.empty();
        }

        return focusSource.getIngredientUnderMouse(input.getMouseX(), input.getMouseY())
                .filter(target -> target.getElement().getBookmark().isPresent())
                .filter(target -> target.getElement().getTypedIngredient().getItemStack().isPresent())
                .findFirst()
                .map(target -> {
                    if (!input.isSimulate()) {
                        target.getElement().getTypedIngredient().getItemStack()
                                .ifPresent(stack -> HandleInteraction.sendPacket(menu, stack, action));
                    }
                    return new SameElementInputHandler((IUserInputHandler) (Object) this, target::isMouseOver);
                });
    }
}
