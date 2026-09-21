package me.myogoo.ae2tb.integration.ae2;

import appeng.core.network.ServerboundPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import com.mojang.logging.LogUtils;
import me.myogoo.ae2tb.network.serverbound.AE2TBInteractionPacket;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

// 이름 추천좀...
public class HandleInteraction {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void sendPacket(MEStorageMenu menu, ItemStack stack, InventoryAction action) {
        if (stack.isEmpty()) return;
        if (!menu.isClientSide()) return;
        if (action == null) return;

        ServerboundPacket packet = new AE2TBInteractionPacket(menu.containerId, stack, action);
        PacketDistributor.sendToServer(packet);
    }
}
