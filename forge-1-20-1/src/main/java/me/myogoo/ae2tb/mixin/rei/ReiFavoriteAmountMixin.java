package me.myogoo.ae2tb.mixin.rei;

import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.menu.me.common.MEStorageMenu;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.myotus.api.MyotusAPI;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import me.shedaniel.rei.impl.client.gui.widget.favorites.listeners.FavoritesRegionListener;
import me.shedaniel.rei.impl.client.gui.widget.favorites.listeners.FavoritesSystemRegionListener;
import me.shedaniel.rei.impl.client.gui.widget.region.EntryStacksRegionWidget;
import me.shedaniel.rei.impl.client.gui.widget.region.RegionEntryWidget;
import me.shedaniel.rei.impl.client.gui.widget.region.RegionListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntryStacksRegionWidget.class)
public abstract class ReiFavoriteAmountMixin {
    @Final
    @Shadow(remap = false)
    public RegionListener<?> listener;

    @Shadow(remap = false)
    private List<RegionEntryWidget<?>> entriesList;

    @Unique
    private static final float AE2TB_AMOUNT_SCALE = 0.65F;

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/impl/client/gui/widget/BatchedEntryRendererManager;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
                    shift = At.Shift.AFTER,
                    remap = false),
            require = 1)
    private void renderAE2Amount(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!(listener instanceof FavoritesRegionListener) && !(listener instanceof FavoritesSystemRegionListener)) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        if (!ScreenContexts.shouldShowBookmarkAmounts(minecraft.screen, DisplayScreen.class)) {
            return;
        }

        var player = minecraft.player;
        if (player == null || !(player.containerMenu instanceof MEStorageMenu menu)) {
            return;
        }

        if (!ae2tb$canRenderAmount(menu)) {
            return;
        }

        var bounds = ((EntryStacksRegionWidget<?>) (Object) this).getBounds();
        for (var widget : entriesList) {
            if (widget.getBounds().getMaxY() >= bounds.getY() && widget.getBounds().y <= bounds.getMaxY()) {
                ae2tb$drawWidgetAmount(guiGraphics, menu, widget);
            }
        }
    }

    @Unique
    private void ae2tb$drawWidgetAmount(GuiGraphics guiGraphics, MEStorageMenu menu, EntryWidget widget) {
        EntryStack<?> entryStack = widget.getCurrentEntry();
        var genericStack = ae2tb$toGenericStack(entryStack);
        if (genericStack == null) {
            return;
        }

        var entries = menu.getClientRepo().getAllEntries();
        if (entries == null) {
            return;
        }

        for (var entry : entries) {
            if (entry.getWhat() != null && entry.getWhat().matches(genericStack)) {
                ae2tb$drawAmount(guiGraphics, widget, entry.getWhat().formatAmount(entry.getStoredAmount(), AmountFormat.SLOT));
                return;
            }
        }
    }

    @Unique
    private GenericStack ae2tb$toGenericStack(EntryStack<?> entryStack) {
        if (entryStack == null || entryStack.isEmpty()) {
            return null;
        }

        ItemStack itemStack;
        if (entryStack.getValueType() == ItemStack.class) {
            itemStack = entryStack.castValue();
        } else {
            var cheatsAs = entryStack.cheatsAs();
            itemStack = cheatsAs.isEmpty() ? ItemStack.EMPTY : cheatsAs.getValue();
        }

        return itemStack.isEmpty() ? null : GenericStack.fromItemStack(itemStack);
    }

    @Unique
    private void ae2tb$drawAmount(GuiGraphics guiGraphics, EntryWidget widget, String amount) {
        if (amount == null || amount.isBlank()) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;
        var bounds = widget.getInnerBounds();
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
                || MyotusAPI.terminalUpgrades().hasUpgrade(menu, AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()));
    }
}
