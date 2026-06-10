package me.myogoo.ae2tb.init;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.client.TranslateKey;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.client.gui.widgets.KeyBindingButton;
import me.myogoo.myotus.api.config.MyoConfigTab;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.world.item.ItemStack;

public class AE2TBConfigTab implements MyoConfigTabScreen {
    public static void initialize() {
        MyotusAPI.configTabs().registerTerminalConfigTab(new MyoConfigTab(
                AE2TB.makeId("terminal_bookmarks"),
                TranslateKey.CATEGORY.getTranslate(),
                new ItemStack(AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()),
                "ae2tb.json",
                new AE2TBConfigTab()
        ));
    }

    @Override
    public void buildTab(WidgetContainer widget, AEBaseScreen<?> screen) {
        widget.add("binding:pickup_single",
                new KeyBindingButton(TranslateKey.PICKUP_SINGLE_ITEM.getTranslate(), keys -> {
                }));
        widget.add("binding:pickup_set",
                new KeyBindingButton(TranslateKey.PICKUP_SET_ITEM.getTranslate(), keys -> {
                }));
        widget.add("binding:picked_autocrafting",
                new KeyBindingButton(TranslateKey.PICKED_ITEM_AUTOCRAFTING.getTranslate(), keys -> {
                }));
    }
}
