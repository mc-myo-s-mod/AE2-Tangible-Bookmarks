package me.myogoo.ae2tb.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public final class ScreenContexts {
    private static final String GUIDEME_SCREEN_PACKAGE = "guideme.internal.screen.";

    private ScreenContexts() {
    }

    /** @deprecated Use {@link #isTerminalOrRecipeViewerScreen(Screen, Class)} for either amount overlay. */
    @Deprecated(forRemoval = true)
    public static boolean shouldShowBookmarkAmounts(Screen screen, Class<?> recipeViewerScreenType) {
        return isTerminalOrRecipeViewerScreen(screen, recipeViewerScreenType);
    }

    public static boolean isTerminalOrRecipeViewerScreen(Screen screen, Class<?> recipeViewerScreenType) {
        if (screen == null || isGuideMeScreen(screen)) {
            return false;
        }

        return screen instanceof AbstractContainerScreen<?> || recipeViewerScreenType.isInstance(screen);
    }

    private static boolean isGuideMeScreen(Screen screen) {
        return screen.getClass().getName().startsWith(GUIDEME_SCREEN_PACKAGE);
    }
}
