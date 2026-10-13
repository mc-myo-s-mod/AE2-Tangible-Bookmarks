package me.myogoo.ae2tb.gametest;

import appeng.client.api.integrations.jei.IngredientConverters;
import com.mojang.logging.LogUtils;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.client.ScreenContexts;
import mezz.jei.gui.overlay.ingredients.IngredientGrid;
import mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigationController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class Ae2tbJeiMixinClientAudit {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean audited;

    public Ae2tbJeiMixinClientAudit() {
        NeoForge.EVENT_BUS.addListener(Ae2tbJeiMixinClientAudit::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (audited || minecraft.getOverlay() != null || !(minecraft.screen instanceof TitleScreen)
                || !Boolean.getBoolean("ae2tb.jeiMixinAudit")) {
            return;
        }
        audited = true;

        if (!ModList.get().isLoaded("jei")) {
            throw new AssertionError("JEI mixin audit requires JEI");
        }
        boolean hasItemConverter = IngredientConverters.getConverters().stream()
                .anyMatch(converter -> converter.getIngredientType().getIngredientClass() == ItemStack.class);
        boolean hasFluidConverter = IngredientConverters.getConverters().stream()
                .anyMatch(converter -> converter.getIngredientType().getIngredientClass() == FluidStack.class);
        if (!hasItemConverter || !hasFluidConverter) {
            throw new AssertionError("AE2 JEI item/fluid converters were not registered");
        }

        MixinEnvironment.getCurrentEnvironment().audit();
        boolean applied = java.util.Arrays.stream(IngredientGridWithNavigationController.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().contains("ae2tb$handleBookmarkedGridInput"));
        if (!applied) {
            throw new AssertionError("AE2TB JEI grid input mixin was not applied");
        }
        boolean amountMixinApplied = java.util.Arrays.stream(IngredientGrid.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().contains("ae2tb$drawGridAmounts"));
        if (!amountMixinApplied) {
            throw new AssertionError("AE2TB JEI grid amount mixin was not applied");
        }
        verifyIndependentAmountOptions();
        checkLegacyScreenContext(minecraft);

        LOGGER.info("AE2TB JEI mixin client audit PASS");
        minecraft.stop();
    }

    private static void verifyIndependentAmountOptions() {
        boolean bookmarks = AE2TBConfig.showBookmarkAmounts();
        boolean ingredientList = AE2TBConfig.showIngredientListAmounts();
        try {
            AE2TBConfig.CLIENT.showBookmarkAmounts.set(false);
            AE2TBConfig.CLIENT.showIngredientListAmounts.set(true);
            if (AE2TBConfig.showBookmarkAmounts() || !AE2TBConfig.showIngredientListAmounts()) {
                throw new AssertionError("Ingredient-list amounts depend on bookmark visibility");
            }
            AE2TBConfig.CLIENT.showBookmarkAmounts.set(true);
            AE2TBConfig.CLIENT.showIngredientListAmounts.set(false);
            if (!AE2TBConfig.showBookmarkAmounts() || AE2TBConfig.showIngredientListAmounts()) {
                throw new AssertionError("Bookmark amounts depend on ingredient-list visibility");
            }
        } finally {
            AE2TBConfig.CLIENT.showBookmarkAmounts.set(bookmarks);
            AE2TBConfig.CLIENT.showIngredientListAmounts.set(ingredientList);
        }
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
