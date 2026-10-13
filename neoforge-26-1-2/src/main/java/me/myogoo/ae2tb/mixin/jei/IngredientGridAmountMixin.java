package me.myogoo.ae2tb.mixin.jei;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import appeng.client.integrations.jei.GenericEntryStackHelper;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.myotus.api.MyotusAPI;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.overlay.ingredients.IngredientGrid;
import mezz.jei.gui.overlay.ingredients.IngredientListSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Mixin(IngredientGrid.class)
public abstract class IngredientGridAmountMixin {
    @Final
    @Shadow
    private boolean searchable;

    @Shadow
    public abstract Stream<IngredientListSlot> getSlots();

    @Unique
    private static final float AE2TB_AMOUNT_SCALE = 0.65F;

    @Inject(
            method = "drawContents(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
            at = @At("TAIL"),
            require = 1,
            expect = 1,
            remap = false
    )
    private void ae2tb$drawGridAmounts(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY,
                                     CallbackInfo ci) {
        if (!ScreenContexts.isTerminalOrRecipeViewerScreen(minecraft.screen, RecipesGui.class)) {
            return;
        }

        var player = minecraft.player;
        if (player == null || !(player.containerMenu instanceof MEStorageMenu menu)) {
            return;
        }

        if (!ae2tb$canRenderAmount(menu)) {
            return;
        }

        var entries = menu.getClientRepo().getAllEntries();
        if (entries == null) {
            return;
        }
        Map<AEKey, GridInventoryEntry> entriesByKey = new HashMap<>();
        for (var entry : entries) {
            if (entry.getWhat() != null) {
                entriesByKey.putIfAbsent(entry.getWhat(), entry);
            }
        }
        getSlots().forEach(slot -> ae2tb$drawSlotAmount(guiGraphics, entriesByKey, slot));
    }

    @Unique
    private void ae2tb$drawSlotAmount(GuiGraphicsExtractor guiGraphics,
                                    Map<AEKey, GridInventoryEntry> entriesByKey, IngredientListSlot slot) {
        var optionalElement = slot.getOptionalElement();
        if (optionalElement.isEmpty()) {
            return;
        }

        var element = optionalElement.get();
        if (slot.isBlocked() || (!searchable && element.getBookmark().isEmpty())) {
            return;
        }

        var genericStack = GenericEntryStackHelper.ingredientToStack(element.getTypedIngredient());
        if (genericStack == null) {
            return;
        }

        var entry = entriesByKey.get(genericStack.what());
        if (entry != null) {
            ae2tb$drawAmount(guiGraphics, slot.getRenderArea(), genericStack.what().formatAmount(entry.getStoredAmount(), AmountFormat.SLOT));
        }
    }

    @Unique
    private void ae2tb$drawAmount(GuiGraphicsExtractor guiGraphics, ImmutableRect2i bounds, String amount) {
        if (amount == null || amount.isBlank()) {
            return;
        }

        var font = Minecraft.getInstance().font;
        float x = bounds.getX() + bounds.getWidth() + 1.0F - font.width(amount) * AE2TB_AMOUNT_SCALE;
        float y = bounds.getY() + bounds.getHeight() - 5.0F;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(AE2TB_AMOUNT_SCALE);
        guiGraphics.text(font, amount, 0, 0, 0xFFFFFFFF, true);
        guiGraphics.pose().popMatrix();
    }

    @Unique
    private boolean ae2tb$canRenderAmount(MEStorageMenu menu) {
        return AE2TBConfig.enableBookmarkAmountCounting()
                && (searchable ? AE2TBConfig.showIngredientListAmounts() : AE2TBConfig.showBookmarkAmounts())
                && (AE2TBConfig.QoL()
                || MyotusAPI.terminalUpgrades().hasUpgrade(menu, AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()));
    }
}
