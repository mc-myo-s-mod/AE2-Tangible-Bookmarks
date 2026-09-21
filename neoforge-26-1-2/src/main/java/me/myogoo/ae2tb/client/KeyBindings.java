package me.myogoo.ae2tb.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.myogoo.ae2tb.AE2TB;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(AE2TB.MODID, AE2TB.MODID));

    public static final KeyMapping PICKUP_SINGLE_ITEM = new KeyMapping(
            TranslateKey.PICKUP_SINGLE_ITEM.key(),
            KeyConflictContext.GUI,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            CATEGORY
    );

    public static final KeyMapping PICKUP_SET_ITEM = new KeyMapping(
            TranslateKey.PICKUP_SET_ITEM.key(),
            KeyConflictContext.GUI,
            KeyModifier.SHIFT,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            CATEGORY
    );

    public static final KeyMapping PICKED_ITEM_AUTOCRAFTING = new KeyMapping(
            TranslateKey.PICKED_ITEM_AUTOCRAFTING.key(),
            KeyConflictContext.GUI,
            KeyModifier.CONTROL,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            CATEGORY
    );
}
