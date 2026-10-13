package me.myogoo.ae2tb.gametest;

import com.mojang.logging.LogUtils;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.client.ScreenContexts;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.handlers.BookmarkInputHandler;
import mezz.jei.gui.input.handlers.FocusInputHandler;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.ingredients.IngredientGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.invoke.MethodHandles;

public final class Ae2tbJeiMixinClientAudit {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean audited;

    public Ae2tbJeiMixinClientAudit() {
        NeoForge.EVENT_BUS.addListener(Ae2tbJeiMixinClientAudit::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (audited || !(minecraft.screen instanceof TitleScreen) || !Boolean.getBoolean("ae2tb.jeiMixinAudit")) {
            return;
        }
        audited = true;

        checkAmountOptions();
        checkLegacyScreenContext(minecraft);
        if (!ModList.get().isLoaded("jei") && !ModList.get().isLoaded("emi")
                && !ModList.get().isLoaded("roughlyenoughitems")) {
            throw new AssertionError("Recipe-viewer mixin audit requires JEI, EMI or REI");
        }

        try {
            var lookup = MethodHandles.lookup();
            if (ModList.get().isLoaded("jei")) {
                lookup.ensureInitialized(FocusInputHandler.class);
                lookup.ensureInitialized(BookmarkInputHandler.class);
                lookup.ensureInitialized(CombinedRecipeFocusSource.class);
                lookup.ensureInitialized(BookmarkOverlay.class);
                lookup.ensureInitialized(IngredientGrid.class);
            }
            if (ModList.get().isLoaded("emi")) {
                Class.forName("dev.emi.emi.screen.EmiScreenManager$ScreenSpace");
            }
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to initialize JEI mixin targets", e);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Unable to initialize EMI mixin targets", e);
        }

        MixinEnvironment.getCurrentEnvironment().audit();
        if (ModList.get().isLoaded("roughlyenoughitems")) {
            ReiKeyConversionClientProbe.run();
        }
        LOGGER.info("AE2TB recipe-viewer mixin client audit PASS");
        minecraft.stop();
    }

    private static void checkAmountOptions() {
        if (AE2TBConfig.CLIENT.showIngredientListAmounts.getDefault()) {
            throw new AssertionError("Ingredient list amounts must default to false");
        }
        boolean bookmarks = AE2TBConfig.showBookmarkAmounts();
        boolean ingredients = AE2TBConfig.showIngredientListAmounts();
        try {
            AE2TBConfig.CLIENT.showBookmarkAmounts.set(true);
            AE2TBConfig.CLIENT.showIngredientListAmounts.set(false);
            if (!AE2TBConfig.showBookmarkAmounts() || AE2TBConfig.showIngredientListAmounts()) {
                throw new AssertionError("Disabled ingredient amounts must preserve bookmark amounts");
            }
            AE2TBConfig.CLIENT.showBookmarkAmounts.set(false);
            AE2TBConfig.CLIENT.showIngredientListAmounts.set(true);
            if (AE2TBConfig.showBookmarkAmounts() || !AE2TBConfig.showIngredientListAmounts()) {
                throw new AssertionError("Ingredient amounts must be independent of bookmark amounts");
            }
        } finally {
            AE2TBConfig.CLIENT.showBookmarkAmounts.set(bookmarks);
            AE2TBConfig.CLIENT.showIngredientListAmounts.set(ingredients);
        }
        LOGGER.info("AE2TB ingredient amount config checks PASS");
    }
    @SuppressWarnings("deprecation")
    private static void checkLegacyScreenContext(Minecraft minecraft) {
        var screen = minecraft.screen;
        if (ScreenContexts.shouldShowBookmarkAmounts(null, screen.getClass())
                || !ScreenContexts.shouldShowBookmarkAmounts(screen, screen.getClass())
                || ScreenContexts.shouldShowBookmarkAmounts(screen, net.minecraft.client.gui.screens.inventory.AbstractContainerScreen.class)
                || ScreenContexts.shouldShowBookmarkAmounts(screen, screen.getClass())
                != ScreenContexts.isTerminalOrRecipeViewerScreen(screen, screen.getClass())) {
            throw new AssertionError("Legacy screen-context API must preserve rejected and accepted screen behavior");
        }
        System.out.println("AE2TB legacy screen-context API checks PASS");
    }
}
