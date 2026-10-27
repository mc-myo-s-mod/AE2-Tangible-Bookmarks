package me.myogoo.ae2tb.mixin.jei;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.ae2tb.integration.jei.AE2TBJeiPlugin;
import me.myogoo.myotus.menu.TerminalUpgradeHelper;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.overlay.ingredients.IngredientGrid;
import mezz.jei.gui.overlay.ingredients.IngredientListSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    @Shadow(remap = false)
    private boolean searchable;

    @Shadow(remap = false)
    public abstract Stream<IngredientListSlot> getSlots();

    @Unique
    private static final float AE2TB_AMOUNT_SCALE = 0.65F;

    @Inject(method = "drawContents(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/GuiGraphics;IIZ)V",
            at = @At("TAIL"), remap = false)
    private void ae2tb$drawAmounts(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, boolean drawSlotBackgrounds, CallbackInfo ci) {
        if (!(searchable ? AE2TBConfig.showIngredientListAmounts() : AE2TBConfig.showBookmarkAmounts())
                || !ScreenContexts.isTerminalOrRecipeViewerScreen(minecraft.screen, RecipesGui.class)) {
            return;
        }
        var player = minecraft.player;
        if (player == null || !(player.containerMenu instanceof MEStorageMenu menu)
                || !AE2TBConfig.enableBookmarkAmountCounting()
                || !(AE2TBConfig.QoL()
                || TerminalUpgradeHelper.hasUpgrade(menu, AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()))) {
            return;
        }
        var entries = menu.getClientRepo().getAllEntries();
        if (entries == null) {
            return;
        }
        Map<AEKey, GridInventoryEntry> amounts = new HashMap<>();
        for (var entry : entries) {
            if (entry.getWhat() != null) {
                amounts.put(entry.getWhat(), entry);
            }
        }
        getSlots().forEach(slot -> ae2tb$drawSlotAmount(guiGraphics, amounts, slot));
    }

    @Unique
    private void ae2tb$drawSlotAmount(GuiGraphics guiGraphics, Map<AEKey, GridInventoryEntry> amounts, IngredientListSlot slot) {
        var optionalElement = slot.getOptionalElement();
        if (optionalElement.isEmpty()) {
            return;
        }
        var element = optionalElement.get();
        if (!searchable && element.getBookmark().isEmpty()) {
            return;
        }
        var genericStack = AE2TBJeiPlugin.toGenericStack(element.getTypedIngredient());
        if (genericStack == null) {
            return;
        }
        var entry = amounts.get(genericStack.what());
        if (entry != null) {
            ae2tb$drawAmount(guiGraphics, slot.getRenderArea(),
                    entry.getWhat().formatAmount(entry.getStoredAmount(), AmountFormat.SLOT));
        }
    }

    @Unique
    private void ae2tb$drawAmount(GuiGraphics guiGraphics, ImmutableRect2i bounds, String amount) {
        if (amount == null || amount.isBlank()) {
            return;
        }
        var font = Minecraft.getInstance().font;
        float x = bounds.getX() + bounds.getWidth() + 1.0F - font.width(amount) * AE2TB_AMOUNT_SCALE;
        float y = bounds.getY() + bounds.getHeight() - 5.0F;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 200.0F);
        guiGraphics.pose().scale(AE2TB_AMOUNT_SCALE, AE2TB_AMOUNT_SCALE, 1.0F);
        guiGraphics.drawString(font, amount, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
    }
}
