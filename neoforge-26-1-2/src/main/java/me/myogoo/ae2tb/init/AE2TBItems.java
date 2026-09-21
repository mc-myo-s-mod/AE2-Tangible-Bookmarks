package me.myogoo.ae2tb.init;

import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.item.TerminalBookmarkInteractCardItem;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class AE2TBItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(AE2TB.MODID);

    public static final DeferredItem<TerminalBookmarkInteractCardItem> TERMINAL_BOOKMARK_INTERACT_CARD = registerItem(
            "terminal_bookmark_interact_card", TerminalBookmarkInteractCardItem::new);

    private AE2TBItems() {
    }

    private static <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> item) {

        var registeredItem = REGISTER.registerItem(name, item);
        MyotusAPI.creativeTabs().registerCreativeTabItem(registeredItem);
        return registeredItem;

    }
}
