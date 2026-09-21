package me.myogoo.ae2tb.mixin.emi;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import com.mojang.blaze3d.platform.InputConstants;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.screen.EmiScreenManager;
import me.myogoo.ae2tb.client.KeyBindings;
import me.myogoo.ae2tb.integration.ae2.HandleInteraction;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiScreenManager.class)
public class EmiScreenManagerMixin {
    @Shadow
    private static Minecraft client;

    @Inject(method = "mouseReleased", at = @At("HEAD"), remap = false, cancellable = true)
    private static void ae2tb$handleMouseReleasedFirst(double mouseX, double mouseY, int button,
            CallbackInfoReturnable<Boolean> cir) {
        if (ae2tb$handleHoveredFavorite(InputConstants.Type.MOUSE.getOrCreate(button),
                (int) mouseX, (int) mouseY, ae2tb$isClickClicky(button))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), remap = false, cancellable = true)
    private static void ae2tb$handleKeyPressedFirst(int keyCode, int scanCode, int modifiers,
            CallbackInfoReturnable<Boolean> cir) {
        if (ae2tb$handleHoveredFavorite(InputConstants.getKey(keyCode, scanCode),
                EmiScreenManager.lastMouseX, EmiScreenManager.lastMouseY, true)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean ae2tb$handleHoveredFavorite(InputConstants.Key input, int mouseX, int mouseY, boolean notClick) {
        var localPlayer = client.player;
        if (!(localPlayer != null && localPlayer.containerMenu instanceof MEStorageMenu menu)) {
            return false;
        }

        var stack = EmiScreenManager.getHoveredStack(mouseX, mouseY, notClick);
        if (!(stack instanceof EmiScreenManager.SidebarEmiStackInteraction sidebar)
                || sidebar.getType() != SidebarType.FAVORITES) {
            return false;
        }

        EmiIngredient ingredient = stack.getStack();
        if (ingredient.getEmiStacks().isEmpty()) {
            return false;
        }

        var itemStack = ingredient.getEmiStacks().get(0).getItemStack();
        if (ae2tb$matches(KeyBindings.PICKED_ITEM_AUTOCRAFTING, input)) {
            HandleInteraction.sendPacket(menu, itemStack, InventoryAction.AUTO_CRAFT);
            return true;
        }
        if (ae2tb$matches(KeyBindings.PICKUP_SET_ITEM, input)) {
            HandleInteraction.sendPacket(menu, itemStack, InventoryAction.SHIFT_CLICK);
            return true;
        }
        if (ae2tb$matches(KeyBindings.PICKUP_SINGLE_ITEM, input)) {
            HandleInteraction.sendPacket(menu, itemStack, InventoryAction.PICKUP_SINGLE);
            return true;
        }
        return false;
    }

    @Unique
    private static boolean ae2tb$matches(KeyMapping keyMapping, InputConstants.Key input) {
        return keyMapping.isActiveAndMatches(input);
    }

    @Unique
    private static boolean ae2tb$isClickClicky(int button) {
        return button >= 0 && button < 3;
    }
}
