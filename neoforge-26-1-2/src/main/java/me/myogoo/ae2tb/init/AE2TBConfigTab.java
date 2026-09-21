package me.myogoo.ae2tb.init;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import me.myogoo.ae2tb.client.TranslateKey;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import me.myogoo.myotus.client.gui.widgets.KeyBindingButton;

public class AE2TBConfigTab implements MyoConfigTabScreen {
    private AECheckbox showBookmarkAmountsCheckbox;

    @Override
    public void buildTab(WidgetContainer widget, AEBaseScreen<?> screen) {
        showBookmarkAmountsCheckbox = widget.addCheckbox("show_bookmark_amounts",
                TranslateKey.SHOW_BOOKMARK_AMOUNTS.getTranslate(), this::save);
        widget.add("binding:pickup_single", new KeyBindingButton(TranslateKey.PICKUP_SINGLE_ITEM.getTranslate(), keys -> {}));
        widget.add("binding:pickup_set", new KeyBindingButton(TranslateKey.PICKUP_SET_ITEM.getTranslate(), keys -> {}));
        widget.add("binding:picked_autocrafting", new KeyBindingButton(TranslateKey.PICKED_ITEM_AUTOCRAFTING.getTranslate(), keys -> {}));
        updateState();
    }

    protected void updateState() {
        if (showBookmarkAmountsCheckbox != null) {
            showBookmarkAmountsCheckbox.setSelected(AE2TBConfig.showBookmarkAmounts());
        }
    }

    protected void save() {
        if (showBookmarkAmountsCheckbox != null) {
            AE2TBConfig.CLIENT.showBookmarkAmounts.set(showBookmarkAmountsCheckbox.isSelected());
        }
        AE2TBConfig.CLIENT.get().save();
        updateState();
    }
}
