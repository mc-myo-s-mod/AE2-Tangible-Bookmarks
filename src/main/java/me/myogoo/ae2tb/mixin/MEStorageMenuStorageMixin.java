package me.myogoo.ae2tb.mixin;

import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MEStorageMenu.class)
public interface MEStorageMenuStorageMixin {
    @Accessor("storage")
    MEStorage getStorage();

    @Invoker("handleNetworkInteraction")
    void callHandleNetworkInteraction(ServerPlayer player, AEKey clickedKey, InventoryAction action);
}
