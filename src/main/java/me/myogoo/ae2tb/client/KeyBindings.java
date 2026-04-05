package me.myogoo.ae2tb.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping PICKUP_SINGLE_ITEM = new KeyMapping(
            TranslateKey.PICKUP_SINGLE_ITEM.key(),
            KeyConflictContext.GUI,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            TranslateKey.CATEGORY.key());

    public static final KeyMapping PICKUP_SET_ITEM = new KeyMapping(
            TranslateKey.PICKUP_SET_ITEM.key(),
            KeyConflictContext.GUI,
            KeyModifier.SHIFT,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            TranslateKey.CATEGORY.key());

    public static final KeyMapping PICKED_ITEM_AUTOCRAFTING = new KeyMapping(
            TranslateKey.PICKED_ITEM_AUTOCRAFTING.key(),
            KeyConflictContext.GUI,
            KeyModifier.CONTROL,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            TranslateKey.CATEGORY.key());
}
