package me.myogoo.ae2tb.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum TranslateKey {
    CATEGORY("key.categories.ae2tb"),

    PICKUP_SET_ITEM("key.ae2tb.pickup_set_item"),
    PICKUP_SINGLE_ITEM("key.ae2tb.pickup_single_item"),
    PICKED_ITEM_AUTOCRAFTING("key.ae2tb.picked_item_autocrafting"),
    SHOW_BOOKMARK_AMOUNTS("gui.ae2tb.config.show_bookmark_amounts"),

    TERMINAL_BOOKMARK_INTERACT_CARD_DESC("item.ae2tb.terminal_bookmark_interact_card.desc");

    private final String key;

    TranslateKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public MutableComponent getTranslate() {
        return Component.translatable(this.key);
    }
}
