package me.myogoo.ae2tb.mixin.rei;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.client.KeyBindings;
import me.myogoo.ae2tb.integration.ae2.HandleInteraction;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.impl.client.gui.ScreenOverlayImpl;
import me.shedaniel.rei.impl.client.gui.widget.favorites.FavoritesListWidget;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenOverlayImpl.class)
public abstract class ReiScreenOverlayImplMixin {

    @Shadow
    @Final
    private List<Widget> widgets;

    @Inject(method = "mouseClicked", at = @At(value = "TAIL"), remap = false, cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            cir.setReturnValue(false);
            return;
        }

        if (!(player.containerMenu instanceof MEStorageMenu menu)) {
            cir.setReturnValue(false);
            return;
        }

        var favoritesWidget = this.widgets.stream()
                .filter(widget -> widget instanceof FavoritesListWidget)
                .map(widget -> (FavoritesListWidget) widget)
                .findFirst().orElse(null);
        if (favoritesWidget == null) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack stack = favoritesWidget.getFocusedStack().cheatsAs().castValue();
        if (stack == null || stack.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }
        if (ae2tb$isClicked(KeyBindings.PICKUP_SINGLE_ITEM, button)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.PICKUP_SINGLE);
            cir.setReturnValue(true);
            return;
        }
        if (ae2tb$isClicked(KeyBindings.PICKUP_SET_ITEM, button)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.SHIFT_CLICK);
            cir.setReturnValue(true);
            return;
        }
        if (ae2tb$isClicked(KeyBindings.PICKED_ITEM_AUTOCRAFTING, button)) {
            HandleInteraction.sendPacket(menu, stack, InventoryAction.AUTO_CRAFT);
            cir.setReturnValue(true);
            return;
        }
    }

    @Unique
    private boolean ae2tb$isClicked(KeyMapping keyMapping, int button) {
        return keyMapping.matchesMouse(button) && keyMapping.getKeyModifier().isActive(KeyConflictContext.GUI);
    }
}
