package me.myogoo.ae2tb.integration.emi;

import com.mojang.blaze3d.platform.InputConstants;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.input.EmiInput;
import me.myogoo.ae2tb.client.TranslateKey;

public class AE2TBEmiBind {
    @EmiConfig.ConfigGroup("ae2tb")
    @EmiConfig.Comment("Pick up a single item with middle mouse click")
    @EmiConfig.ConfigValue("ae2tb.pickup_single_item")
    public static EmiBind PICKUP_SINGLE_ITEM = new EmiBind(
            TranslateKey.PICKUP_SINGLE_ITEM.key(),
            new EmiBind.ModifiedKey(InputConstants.Type.MOUSE.getOrCreate(2), 0));

    @EmiConfig.Comment("Pick up an item set with Shift + middle click")
    @EmiConfig.ConfigValue("ae2tb.pickup_set_item")
    public static EmiBind PICKUP_SET_ITEM = new EmiBind(
            TranslateKey.PICKUP_SET_ITEM.key(),
            new EmiBind.ModifiedKey(InputConstants.Type.MOUSE.getOrCreate(2), EmiInput.SHIFT_MASK));

    @EmiConfig.ConfigGroupEnd
    @EmiConfig.Comment("Pick up the autocraft target item with Ctrl + middle click")
    @EmiConfig.ConfigValue("ae2tb.pickup_autocraft_item")
    public static EmiBind PICKED_ITEM_AUTOCRAFTING = new EmiBind(
            TranslateKey.PICKED_ITEM_AUTOCRAFTING.key(),
            new EmiBind.ModifiedKey(InputConstants.Type.MOUSE.getOrCreate(2), EmiInput.CONTROL_MASK));
}
