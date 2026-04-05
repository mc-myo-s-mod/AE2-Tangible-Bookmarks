package me.myogoo.ae2tb.mixin.emi;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.input.EmiBind;
import me.myogoo.ae2tb.integration.ae2.HandleInteraction;
import me.myogoo.ae2tb.integration.emi.AE2TBEmiBind;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EmiScreenManager.class)
public class EmiScreenManagerMixin {
    @Shadow
    private static Minecraft client;

    @Inject(method = "stackInteraction", at = @At(value = "TAIL"), remap = false, cancellable = true)
    private static void handleAEInteraction(EmiStackInteraction stack, Function<EmiBind, Boolean> function,
            CallbackInfoReturnable<Boolean> cir) {
        EmiIngredient ingredient = stack.getStack();
        var localPlayer = client.player;
        if (!(stack instanceof EmiScreenManager.SidebarEmiStackInteraction sidebar)) {
            cir.setReturnValue(false);
            return;
        }

        if (sidebar.getType() != SidebarType.FAVORITES) {
            cir.setReturnValue(false);
            return;
        }

        if (localPlayer != null && localPlayer.containerMenu instanceof MEStorageMenu menu) {
            if (ingredient.getEmiStacks().isEmpty()) {
                cir.setReturnValue(false);
                return;
            }
            var emiStack = ingredient.getEmiStacks().get(0).getItemStack();
            if (function.apply(AE2TBEmiBind.PICKUP_SINGLE_ITEM)) {
                HandleInteraction.sendPacket(menu, emiStack, InventoryAction.PICKUP_SINGLE);
                cir.setReturnValue(true);
                return;
            }
            if (function.apply(AE2TBEmiBind.PICKUP_SET_ITEM)) {
                HandleInteraction.sendPacket(menu, emiStack, InventoryAction.SHIFT_CLICK);
                cir.setReturnValue(true);
                return;
            }
            if (function.apply(AE2TBEmiBind.PICKED_ITEM_AUTOCRAFTING)) {
                HandleInteraction.sendPacket(menu, emiStack, InventoryAction.AUTO_CRAFT);
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
