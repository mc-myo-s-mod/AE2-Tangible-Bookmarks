package me.myogoo.ae2tb.init;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import me.myogoo.ae2tb.client.TranslateKey;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.MyotusAPI.Client.Widgets;
import me.myogoo.myotus.api.config.MyoConfigTab;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.world.item.ItemStack;

public class AE2TBConfigTab implements MyoConfigTabScreen {
    public static void initialize() {
        MyotusAPI.REGISTER.configRegistrar().registerTerminalConfigTab(new MyoConfigTab(
                TranslateKey.CATEGORY.getTranslate(),
                new ItemStack(AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()),
                "ae2tb.json",
                new AE2TBConfigTab()
        ));
    }

    @Override
    public void buildTab(WidgetContainer widget, AEBaseScreen<?> screen) {
        widget.add("binding:pickup_single",
                Widgets.keyBindingButton(TranslateKey.PICKUP_SINGLE_ITEM.getTranslate(), keys -> {
                }));
        widget.add("binding:pickup_set",
                Widgets.keyBindingButton(TranslateKey.PICKUP_SET_ITEM.getTranslate(), keys -> {
                }));
        widget.add("binding:picked_autocrafting",
                Widgets.keyBindingButton(TranslateKey.PICKED_ITEM_AUTOCRAFTING.getTranslate(), keys -> {
                }));
    }
}
