package me.myogoo.ae2tb.init;

import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.item.TerminalBookmarkInteractCardItem;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class AE2TBItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AE2TB.MODID);

    public static final RegistryObject<TerminalBookmarkInteractCardItem> TERMINAL_BOOKMARK_INTERACT_CARD =
            registerItem("terminal_bookmark_interact_card", TerminalBookmarkInteractCardItem::new);

    private AE2TBItems() {
    }

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> supplier) {
        var item = ITEMS.register(name, supplier);
        MyotusAPI.REGISTER.creativeTabRegistrar().creativeTabItem(item);
        return item;
    }
}
