package me.myogoo.ae2tb.mixin.rei;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import com.mojang.blaze3d.platform.InputConstants;
import me.myogoo.ae2tb.client.KeyBindings;
import me.myogoo.ae2tb.integration.ae2.HandleInteraction;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.impl.client.gui.ScreenOverlayImpl;
import me.shedaniel.rei.impl.client.gui.widget.favorites.FavoritesListWidget;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenOverlayImpl.class)
public abstract class ScreenOverlayImplMixin {

    @Shadow
    @Final
    private List<Widget> widgets;

    @Inject(
            method = "mouseClicked",
            at = @At(value = "HEAD"),
            remap = false,
            cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (!(player.containerMenu instanceof MEStorageMenu menu)) {
            return;
        }

        var favoritesWidget = ae2tb$getFavoritesWidget();
        if (favoritesWidget == null) {
            return;
        }

        ItemStack stack = favoritesWidget.getFocusedStack().cheatsAs().castValue();
        if (stack == null || stack.isEmpty()) {
            return;
        }

        if (isClicked(KeyBindings.PICKED_ITEM_AUTOCRAFTING, button)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.AUTO_CRAFT);
            cir.setReturnValue(true);
            return;
        }
        if (isClicked(KeyBindings.PICKUP_SET_ITEM, button)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.SHIFT_CLICK);
            cir.setReturnValue(true);
            return;
        }
        if (isClicked(KeyBindings.PICKUP_SINGLE_ITEM, button)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.PICKUP_SINGLE);
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "keyPressed",
            at = @At(value = "HEAD"),
            remap = false,
            cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (!(player.containerMenu instanceof MEStorageMenu menu)) {
            return;
        }

        var favoritesWidget = ae2tb$getFavoritesWidget();
        if (favoritesWidget == null) {
            return;
        }

        ItemStack stack = favoritesWidget.getFocusedStack().cheatsAs().castValue();
        if (stack == null || stack.isEmpty()) {
            return;
        }

        if (isPressed(KeyBindings.PICKED_ITEM_AUTOCRAFTING, keyCode, scanCode)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.AUTO_CRAFT);
            cir.setReturnValue(true);
            return;
        }
        if (isPressed(KeyBindings.PICKUP_SET_ITEM, keyCode, scanCode)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.SHIFT_CLICK);
            cir.setReturnValue(true);
            return;
        }
        if (isPressed(KeyBindings.PICKUP_SINGLE_ITEM, keyCode, scanCode)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.PICKUP_SINGLE);
            cir.setReturnValue(true);
        }
    }

    @Unique
    private FavoritesListWidget ae2tb$getFavoritesWidget() {
        return this.widgets.stream()
                .filter(widget -> widget instanceof FavoritesListWidget)
                .map(widget -> (FavoritesListWidget) widget)
                .findFirst().orElse(null);
    }

    @Unique
    private boolean isClicked(KeyMapping keyMapping, int button) {
        return keyMapping.isActiveAndMatches(InputConstants.Type.MOUSE.getOrCreate(button));
    }

    @Unique
    private boolean isPressed(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }
}
