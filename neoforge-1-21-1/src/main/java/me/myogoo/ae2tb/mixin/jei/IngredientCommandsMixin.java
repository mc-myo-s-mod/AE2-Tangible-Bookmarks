package me.myogoo.ae2tb.mixin.jei;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.client.KeyBindings;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.myogoo.ae2tb.integration.ae2.HandleInteraction;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.input.UserInput;
import mezz.jei.common.input.interaction.IInputInteraction;
import mezz.jei.common.input.interaction.InputAction;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IngredientFocusSource;
import mezz.jei.gui.input.InputCommands;
import mezz.jei.gui.input.handlers.IngredientCommands;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(IngredientCommands.class)
public class IngredientCommandsMixin {
    @Final
    @Shadow(remap = false)
    private IngredientFocusSource focusSource;

    @Final
    @Shadow(remap = false)
    private IInternalKeyMappings keys;

    @Inject(method = "registerInputCommands(Lmezz/jei/gui/input/InputCommands;)V",
            at = @At("HEAD"), require = 1, expect = 1, remap = false)
    private void ae2tb$registerBookmarkCommands(InputCommands commands, CallbackInfo ci) {
        ae2tb$registerAction(commands, KeyBindings.PICKED_ITEM_AUTOCRAFTING, InventoryAction.AUTO_CRAFT);
        ae2tb$registerAction(commands, KeyBindings.PICKUP_SET_ITEM, InventoryAction.SHIFT_CLICK);
        ae2tb$registerAction(commands, KeyBindings.PICKUP_SINGLE_ITEM, InventoryAction.PICKUP_SINGLE);
    }

    @Unique
    private void ae2tb$registerAction(InputCommands commands, KeyMapping key, InventoryAction action) {
        commands.add("AE2TB " + action, input -> input.is(key),
                (screen, input) -> ae2tb$prepareBookmarkAction(screen, input, action));
    }

    @Unique
    private Optional<IInputInteraction> ae2tb$prepareBookmarkAction(Screen screen, UserInput input, InventoryAction action) {
        if (!ScreenContexts.isTerminalOrRecipeViewerScreen(screen, RecipesGui.class)) {
            return Optional.empty();
        }
        var minecraft = screen.getMinecraft();
        var player = minecraft.player;
        if (player == null || !(player.containerMenu instanceof MEStorageMenu menu)) {
            return Optional.empty();
        }

        return focusSource.getIngredientUnderMouse(input, keys)
                .filter(IClickableIngredientInternal::canClickToFocus)
                .filter(target -> target.getElement().isVisible())
                .filter(target -> target.getElement().getBookmark().isPresent())
                .filter(target -> target.getTypedIngredient().getItemStack().isPresent())
                .findFirst()
                .map(target -> InputAction.run(() -> target.getTypedIngredient().getItemStack()
                                .ifPresent(stack -> HandleInteraction.sendPacket(menu, stack, action)))
                        .within(target::isMouseOver)
                        .when(target.getElement()::isVisible)
                        .when(() -> minecraft.screen == screen && minecraft.player == player
                                && player.containerMenu == menu));
    }
}
