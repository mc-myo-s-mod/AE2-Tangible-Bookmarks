package me.myogoo.ae2tb.gametest;

import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.shedaniel.rei.api.client.favorites.FavoriteEntry;
import me.shedaniel.rei.api.client.favorites.FavoriteEntryType;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.impl.client.gui.widget.favorites.listeners.FavoritesSystemRegionListener;
import me.shedaniel.rei.impl.client.gui.widget.region.EntryStacksRegionWidget;
import me.shedaniel.rei.plugin.client.runtime.DefaultClientRuntimePlugin;
import me.shedaniel.rei.plugin.common.runtime.DefaultRuntimePlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.List;

/** Run with :forge-1-20-1:runClient -PitemListMod=rei (or jei/emi) -Pae2tbClientProbe. */
public final class RecipeViewerClientProbe {
    private boolean done;

    public RecipeViewerClientProbe() {
        MinecraftForge.EVENT_BUS.addListener(this::clientTick);
    }

    private void clientTick(TickEvent.ClientTickEvent event) {
        var minecraft = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || done || minecraft.getOverlay() != null
                || minecraft.screen == null) {
            return;
        }

        done = true;
        checkAmountOptions();
        checkLegacyScreenContext(minecraft);
        // Also transforms lazy recipe-viewer targets before declaring startup successful.
        MixinEnvironment.getCurrentEnvironment().audit();
        if (ModList.get().isLoaded("roughlyenoughitems")) {
            ReiProbe.run(minecraft);
        }
        System.out.println("AE2TB recipe-viewer client mixin audit complete");
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
        System.out.println("AE2TB ingredient amount config checks PASS");
    }

    private static final class ReiProbe {
        private static void run(Minecraft minecraft) {
            var entryTypes = EntryTypeRegistry.getInstance();
            if (!entryTypes.keySet().contains(VanillaEntryTypes.ITEM.getId())) {
                new DefaultRuntimePlugin().registerEntryTypes(entryTypes);
            }

            ReiKeyConversionClientProbe.run();

            var favoriteTypes = FavoriteEntryType.registry();
            if (favoriteTypes.get(FavoriteEntryType.ENTRY_STACK) == null) {
                new DefaultClientRuntimePlugin().registerFavorites(favoriteTypes);
            }

            var region = new EntryStacksRegionWidget<>(new FavoritesSystemRegionListener());
            region.getBounds().setBounds(8, 8, 24, 24);
            region.setEntries(List.of(FavoriteEntry.fromEntryStack(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(Items.STONE)))),
                    EntryStacksRegionWidget.RemovalMode.DISAPPEAR);

            var guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
            region.render(guiGraphics, 0, 0, 0.0F);
            guiGraphics.flush();
            System.out.println("AE2TB REI favorites batched render probe complete");
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
