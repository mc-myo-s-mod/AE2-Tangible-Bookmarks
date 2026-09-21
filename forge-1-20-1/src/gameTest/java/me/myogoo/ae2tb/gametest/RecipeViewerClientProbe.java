package me.myogoo.ae2tb.gametest;

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
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.List;

/** Run with :forge-1-20-1:runClient -PitemListMod=rei (or jei) -Pae2tbClientProbe. */
@Mod("ae2tb_client_probe")
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
        // Also transforms lazy recipe-viewer targets before declaring startup successful.
        MixinEnvironment.getCurrentEnvironment().audit();
        if (ModList.get().isLoaded("roughlyenoughitems")) {
            ReiProbe.run(minecraft);
        }
        System.out.println("AE2TB recipe-viewer client mixin audit complete");
        minecraft.stop();
    }

    private static final class ReiProbe {
        private static void run(Minecraft minecraft) {
            var entryTypes = EntryTypeRegistry.getInstance();
            if (!entryTypes.keySet().contains(VanillaEntryTypes.ITEM.getId())) {
                new DefaultRuntimePlugin().registerEntryTypes(entryTypes);
            }

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
}
