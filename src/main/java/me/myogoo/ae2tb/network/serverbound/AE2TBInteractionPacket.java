package me.myogoo.ae2tb.network.serverbound;

import appeng.api.stacks.AEItemKey;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.ae2tb.mixin.MEStorageMenuStorageMixin;
import me.myogoo.myotus.menu.TerminalUpgradeHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record AE2TBInteractionPacket(
        int containerId,
        ItemStack itemStack,
        InventoryAction action) {
    public static AE2TBInteractionPacket decode(FriendlyByteBuf buffer) {
        var containerId = buffer.readInt();
        var stack = buffer.readItem();
        var action = InventoryAction.values()[buffer.readInt()];
        return new AE2TBInteractionPacket(containerId, stack, action);
    }

    public void write(FriendlyByteBuf data) {
        data.writeInt(containerId);
        data.writeItem(itemStack);
        data.writeInt(action.ordinal());
    }

    public void handleOnServer(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof MEStorageMenu menu) {
                if (!AE2TBConfig.allowBookmarkInteractionWithoutUpgrade()
                        && !TerminalUpgradeHelper.hasUpgrade(menu, AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get())) {
                    return;
                }

                var menuMixin = (MEStorageMenuStorageMixin) menu;
                var key = AEItemKey.of(itemStack);
                menuMixin.callHandleNetworkInteraction(player, key, action);
            }
        });
        context.setPacketHandled(true);
    }
}
