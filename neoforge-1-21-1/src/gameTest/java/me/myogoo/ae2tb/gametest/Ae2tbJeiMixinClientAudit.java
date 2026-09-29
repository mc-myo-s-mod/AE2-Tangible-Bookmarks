package me.myogoo.ae2tb.gametest;

import com.mojang.logging.LogUtils;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.handlers.BookmarkInputHandler;
import mezz.jei.gui.input.handlers.FocusInputHandler;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.invoke.MethodHandles;

@Mod(value = "ae2tb_gametest", dist = Dist.CLIENT)
public final class Ae2tbJeiMixinClientAudit {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean audited;

    public Ae2tbJeiMixinClientAudit() {
        NeoForge.EVENT_BUS.addListener(Ae2tbJeiMixinClientAudit::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (audited || !(minecraft.screen instanceof TitleScreen) || !Boolean.getBoolean("ae2tb.jeiMixinAudit")) {
            return;
        }
        audited = true;

        if (!ModList.get().isLoaded("jei")) {
            throw new AssertionError("JEI mixin audit requires JEI");
        }

        try {
            var lookup = MethodHandles.lookup();
            lookup.ensureInitialized(FocusInputHandler.class);
            lookup.ensureInitialized(BookmarkInputHandler.class);
            lookup.ensureInitialized(CombinedRecipeFocusSource.class);
            lookup.ensureInitialized(BookmarkOverlay.class);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to initialize JEI mixin targets", e);
        }

        MixinEnvironment.getCurrentEnvironment().audit();
        LOGGER.info("AE2TB JEI mixin client audit PASS");
        minecraft.stop();
    }
}
