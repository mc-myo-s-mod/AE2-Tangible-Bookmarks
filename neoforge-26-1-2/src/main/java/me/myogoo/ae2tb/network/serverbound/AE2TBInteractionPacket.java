package me.myogoo.ae2tb.network.serverbound;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.core.network.ServerboundPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.ae2tb.mixin.MEStorageMenuStorageMixin;
import me.myogoo.myotus.menu.TerminalUpgradeHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record AE2TBInteractionPacket(
        int containerId,
        ItemStack itemStack,
        InventoryAction action
) implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, AE2TBInteractionPacket> STREAM_CODEC = StreamCodec.ofMember(
            AE2TBInteractionPacket::write,
            AE2TBInteractionPacket::decode
    );

    public static final Type<AE2TBInteractionPacket> TYPE = new CustomPacketPayload.Type<>(AE2TB.makeId("interaction"));

    public static AE2TBInteractionPacket decode(RegistryFriendlyByteBuf buffer) {
        var containerId = buffer.readInt();
        var stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        var action = buffer.readEnum(InventoryAction.class);
        return new AE2TBInteractionPacket(containerId, stack, action);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeInt(containerId);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(data, itemStack);
        data.writeEnum(action);
    }


    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof MEStorageMenu meStorageMenu) {
            if (!AE2TBConfig.QoL()
                    && !TerminalUpgradeHelper.hasUpgrade(meStorageMenu, AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get())) {
                return;
            }

            var menu = (MEStorageMenuStorageMixin) meStorageMenu;
            AEKey key = AEItemKey.of(itemStack);
            menu.callHandleNetworkInteraction(player, key, action);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
