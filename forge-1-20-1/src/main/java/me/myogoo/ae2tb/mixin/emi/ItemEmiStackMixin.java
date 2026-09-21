package me.myogoo.ae2tb.mixin.emi;

import appeng.api.integrations.emi.EmiStackConverters;
import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.menu.me.common.MEStorageMenu;
import com.llamalad7.mixinextras.sugar.Local;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.screen.RecipeScreen;
import me.myogoo.ae2tb.client.ScreenContexts;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EmiFavorite.class)
public abstract class ItemEmiStackMixin {

    @Shadow
    public abstract List<EmiStack> getEmiStacks();

    @Shadow
    public abstract EmiIngredient getStack();

    @Unique
    private static final float AE2TB_AMOUNT_SCALE = 0.65F;

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/stack/EmiIngredient;render(Lnet/minecraft/client/gui/GuiGraphics;IIFI)V", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false
    )
    public void renderAELabels(GuiGraphics draw, int x, int y, float delta, int flags, CallbackInfo ci, @Local(name = "context") EmiDrawContext context) {
        if (!ae2tb$shouldRenderAmount(flags)) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        if (!ScreenContexts.shouldShowBookmarkAmounts(minecraft.screen, RecipeScreen.class)) {
            return;
        }

        var player = minecraft.player;
        if (player == null || !(player.containerMenu instanceof MEStorageMenu menu)) {
            return;
        }

        if (!ae2tb$canRenderAmount(menu)) {
            return;
        }

        var stacks = getEmiStacks();
        if (stacks.isEmpty()) {
            return;
        }

        var genericStack = ae2tb$toGenericStack(stacks.get(0));
        if (genericStack == null) {
            return;
        }

        var entries = menu.getClientRepo().getAllEntries();
        if (entries == null) {
            return;
        }

        for (var entry : entries) {
            if (entry.getWhat() != null && entry.getWhat().matches(genericStack)) {
                ae2tb$drawAmount(draw, x, y, entry.getWhat().formatAmount(entry.getStoredAmount(), AmountFormat.SLOT));
                return;
            }
        }
    }

    @Unique
    private static GenericStack ae2tb$toGenericStack(EmiStack stack) {
        for (var converter : EmiStackConverters.getConverters()) {
            var genericStack = converter.toGenericStack(stack);
            if (genericStack != null) {
                return genericStack;
            }
        }
        return null;
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

    @Unique
    private boolean ae2tb$canRenderAmount(MEStorageMenu menu) {
        return AE2TBConfig.enableBookmarkAmountCounting()
                && AE2TBConfig.showBookmarkAmounts()
                && (AE2TBConfig.QoL()
                || MyotusAPI.terminalUpgrades().hasUpgrade(menu, AE2TBItems.TERMINAL_BOOKMARK_INTERACT_CARD.get()));
    }

    @Unique
    private boolean ae2tb$shouldRenderAmount(int flags) {
        // EMI uses negative flag values for compact sidebar/favorites renders without vanilla decorations.
        return flags == -4 || flags == -3;
    }
}
