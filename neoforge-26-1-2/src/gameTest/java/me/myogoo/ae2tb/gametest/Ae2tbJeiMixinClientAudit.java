package me.myogoo.ae2tb.gametest;

import appeng.client.api.integrations.jei.IngredientConverters;
import com.mojang.logging.LogUtils;
import mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigationController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

@Mod(value = "ae2tb_gametest", dist = Dist.CLIENT)
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

        LOGGER.info("AE2TB JEI mixin client audit PASS");
        minecraft.stop();
    }
}
