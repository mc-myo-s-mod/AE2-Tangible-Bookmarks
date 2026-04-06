package me.myogoo.ae2tb.integration.ae2;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.init.AE2TBNetwork;
import me.myogoo.ae2tb.network.serverbound.AE2TBInteractionPacket;
import net.minecraft.world.item.ItemStack;

public class HandleInteraction {
    public static void sendPacket(MEStorageMenu menu, ItemStack stack, InventoryAction action) {
        if (stack.isEmpty()) return;
        if (!menu.isClientSide()) return;
        if (action == null) return;

        AE2TBInteractionPacket packet = new AE2TBInteractionPacket(menu.containerId, stack, action);
        AE2TBNetwork.INSTANCE.sendToServer(packet);
    }
}
