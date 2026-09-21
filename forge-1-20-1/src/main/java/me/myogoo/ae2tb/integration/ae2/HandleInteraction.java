package me.myogoo.ae2tb.integration.ae2;

import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import com.mojang.logging.LogUtils;
import me.myogoo.ae2tb.init.AE2TBNetwork;
import me.myogoo.ae2tb.network.serverbound.AE2TBInteractionPacket;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class HandleInteraction {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void sendPacket(MEStorageMenu menu, ItemStack stack, InventoryAction action) {
        if (stack.isEmpty()) return;
        if (!menu.isClientSide()) return;
        if (action == null) return;

        LOGGER.info("AE2TB middle-click interaction: menuId={}, action={}, item={}",
                menu.containerId, action, stack.getItem().builtInRegistryHolder().key().location());
        AE2TBInteractionPacket packet = new AE2TBInteractionPacket(menu.containerId, stack, action);
        AE2TBNetwork.INSTANCE.sendToServer(packet);
    }
}
