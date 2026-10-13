package me.myogoo.ae2tb.gametest;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.core.definitions.AEParts;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.MEStorageMenu;
import appeng.parts.reporting.ItemTerminalPart;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.network.serverbound.AE2TBInteractionPacket;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.minecraft.gametest.framework.GameTest;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("ae2tb")
@PrefixGameTestTemplate(false)
public final class Ae2tbInteractionGameTests {
    @GameTest(template = "empty")
    public static void staleMenuRequestsAreRejected(GameTestHelper helper) {
        var player = FakePlayerFactory.getMinecraft(helper.getLevel());
        var previousMenu = player.containerMenu;
        boolean previousQol = AE2TBConfig.QoL();
        try {
            AE2TBConfig.COMMON.QoL.set(true);
            var original = new RecordingMenu(7, player);
            var current = new RecordingMenu(8, player);
            player.containerMenu = current;

            new AE2TBInteractionPacket(original.containerId, new ItemStack(Items.STONE),
                    InventoryAction.PICKUP_OR_SET_DOWN).handleOnServer(player);
            new AE2TBInteractionPacket(-1, new ItemStack(Items.STONE),
                    InventoryAction.PICKUP_OR_SET_DOWN).handleOnServer(player);
            helper.assertTrue(current.interactions == 0 && original.interactions == 0,
                    "Stale and invalid menu IDs must not reach AE2 interaction");

            new AE2TBInteractionPacket(current.containerId, new ItemStack(Items.STONE),
                    InventoryAction.PICKUP_OR_SET_DOWN).handleOnServer(player);
            helper.assertTrue(current.interactions == 1
                            && AEItemKey.of(Items.STONE).equals(current.key)
                            && current.action == InventoryAction.PICKUP_OR_SET_DOWN,
                    "The current menu ID must dispatch the original key and action exactly once");

            player.containerMenu = player.inventoryMenu;
            new AE2TBInteractionPacket(player.inventoryMenu.containerId, new ItemStack(Items.STONE),
                    InventoryAction.PICKUP_OR_SET_DOWN).handleOnServer(player);
            helper.assertTrue(current.interactions == 1, "Non-ME menus must not handle bookmark requests");
            System.out.println("AE2TB stale/current container packet checks PASS");
            helper.succeed();
        } finally {
            player.containerMenu = previousMenu;
            AE2TBConfig.COMMON.QoL.set(previousQol);
        }
    }

    private static final class RecordingMenu extends MEStorageMenu {
        private int interactions;
        private AEKey key;
        private InventoryAction action;

        private RecordingMenu(int id, ServerPlayer player) {
            super(null, id, player.getInventory(), new ItemTerminalPart(AEParts.TERMINAL.get()), false);
        }

        @Override
        protected void handleNetworkInteraction(ServerPlayer player, AEKey key, InventoryAction action) {
            interactions++;
            this.key = key;
            this.action = action;
        }
    }
}
