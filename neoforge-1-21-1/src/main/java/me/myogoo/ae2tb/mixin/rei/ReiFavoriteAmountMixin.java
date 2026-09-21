package me.myogoo.ae2tb.mixin.rei;

import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.myotus.menu.TerminalUpgradeHelper;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.favorites.FavoriteEntry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import me.shedaniel.rei.impl.client.gui.widget.region.RegionEntryWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionEntryWidget.class)
public abstract class ReiFavoriteAmountMixin {
    @Shadow
    protected abstract FavoriteEntry asFavoriteEntry();

    @Unique
    private static final float AE2TB_AMOUNT_SCALE = 0.65F;

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void renderAE2Amount(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (asFavoriteEntry() == null) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        if (!ScreenContexts.isTerminalOrRecipeViewerScreen(minecraft.screen, DisplayScreen.class)) {
            return;
        }

        var player = minecraft.player;
        if (player == null || !(player.containerMenu instanceof MEStorageMenu menu)) {
            return;
        }

        if (!ae2tb$canRenderAmount(menu)) {
            return;
        }

        EntryStack<?> entryStack = ((EntryWidget) (Object) this).getCurrentEntry();
        if (entryStack == null || entryStack.isEmpty()) {
            return;
        }

        ItemStack itemStack = entryStack.cheatsAs().castValue();
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }

        var genericStack = GenericStack.fromItemStack(itemStack);
        if (genericStack == null) {
            return;
        }

        var entries = menu.getClientRepo().getAllEntries();
        if (entries == null) {
            return;
        }

        for (var entry : entries) {
            if (entry.getWhat() != null && entry.getWhat().matches(genericStack)) {
                ae2tb$drawAmount(guiGraphics, entry.getWhat().formatAmount(entry.getStoredAmount(), AmountFormat.SLOT));
                return;
            }
        }
    }

    @Unique
    private void ae2tb$drawAmount(GuiGraphics guiGraphics, String amount) {
        if (amount == null || amount.isBlank()) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;
        var bounds = ((EntryWidget) (Object) this).getInnerBounds();
        float x = bounds.x + bounds.width + 1.0F - font.width(amount) * AE2TB_AMOUNT_SCALE;
        float y = bounds.y + bounds.height - 5.0F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 200.0F);
        guiGraphics.pose().scale(AE2TB_AMOUNT_SCALE, AE2TB_AMOUNT_SCALE, 1.0F);
        guiGraphics.drawString(font, amount, 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
    }

    @Unique
    private boolean ae2tb$canRenderAmount(MEStorageMenu menu) {
        return AE2TBConfig.enableBookmarkAmountCounting()
                && AE2TBConfig.showBookmarkAmounts()
                && (AE2TBConfig.QoL()
                || TerminalUpgradeHelper.hasUpgrade(menu, AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()));
    }
}
