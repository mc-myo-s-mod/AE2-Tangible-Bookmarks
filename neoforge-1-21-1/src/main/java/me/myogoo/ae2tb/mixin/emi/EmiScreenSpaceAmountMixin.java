package me.myogoo.ae2tb.mixin.emi;

import appeng.api.integrations.emi.EmiStackConverters;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.RecipeScreen;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.myotus.menu.TerminalUpgradeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(EmiScreenManager.ScreenSpace.class)
public abstract class EmiScreenSpaceAmountMixin {
    @Unique
    private static final float AE2TB_AMOUNT_SCALE = 0.65F;

    @Inject(method = "render(Ldev/emi/emi/runtime/EmiDrawContext;IIFI)V", at = @At("TAIL"), remap = false)
    private void ae2tb$drawAmounts(EmiDrawContext context, int mouseX, int mouseY, float delta, int startIndex, CallbackInfo ci) {
        var space = (EmiScreenManager.ScreenSpace) (Object) this;
        var type = space.getType();
        if (!(type == SidebarType.INDEX ? AE2TBConfig.showIngredientListAmounts()
                : type == SidebarType.FAVORITES && AE2TBConfig.showBookmarkAmounts())) {
            return;
        }
        var minecraft = Minecraft.getInstance();
        if (!ScreenContexts.isTerminalOrRecipeViewerScreen(minecraft.screen, RecipeScreen.class)) {
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
        var stacks = space.getStacks();
        int count = Math.min(space.pageSize, stacks.size() - startIndex);
        for (int offset = 0; offset < count; offset++) {
            var ingredient = stacks.get(startIndex + offset);
            // Synthetic favorites render their own amount instead of calling EmiFavorite.render.
            if (type == SidebarType.FAVORITES && ingredient instanceof EmiFavorite.Synthetic) {
                continue;
            }
            var ingredientStacks = ingredient.getEmiStacks();
            if (ingredientStacks.isEmpty()) {
                continue;
            }
            GenericStack genericStack = null;
            for (var converter : EmiStackConverters.getConverters()) {
                genericStack = converter.toGenericStack(ingredientStacks.get(0));
                if (genericStack != null) {
                    break;
                }
            }
            if (genericStack == null) {
                continue;
            }
            var entry = amounts.get(genericStack.what());
            if (entry != null) {
                ae2tb$drawAmount(context.raw(), space.getRawX(offset) + 1, space.getRawY(offset) + 1,
                        entry.getWhat().formatAmount(entry.getStoredAmount(), AmountFormat.SLOT));
            }
        }
    }

    @Unique
    private void ae2tb$drawAmount(GuiGraphics draw, int x, int y, String amount) {
        if (amount == null || amount.isBlank()) {
            return;
        }
        var font = Minecraft.getInstance().font;
        float textX = x + 17.0F - font.width(amount) * AE2TB_AMOUNT_SCALE;
        float textY = y + 11.0F;
        draw.pose().pushPose();
        draw.pose().translate(textX, textY, 200.0F);
        draw.pose().scale(AE2TB_AMOUNT_SCALE, AE2TB_AMOUNT_SCALE, 1.0F);
        draw.drawString(font, amount, 0, 0, 0xFFFFFF, true);
        draw.pose().popPose();
    }
}
