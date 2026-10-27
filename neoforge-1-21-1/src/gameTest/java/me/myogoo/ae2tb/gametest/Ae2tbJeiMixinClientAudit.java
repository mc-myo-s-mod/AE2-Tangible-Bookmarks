package me.myogoo.ae2tb.gametest;

import com.mojang.logging.LogUtils;
import me.myogoo.ae2tb.config.AE2TBConfig;
import me.myogoo.ae2tb.client.ScreenContexts;
import mezz.jei.common.Internal;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.input.InputPhase;
import mezz.jei.common.input.UserInput;
import mezz.jei.common.input.interaction.IInputInteraction;
import mezz.jei.common.input.interaction.InputAction;
import mezz.jei.gui.input.GuiInputController;
import mezz.jei.gui.input.GuiInputScene;
import mezz.jei.gui.input.ITextInputHandler;
import mezz.jei.gui.input.InputArea;
import mezz.jei.gui.input.InputCommands;
import mezz.jei.gui.input.handlers.IngredientCommands;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.ingredients.IngredientGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

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

        checkAmountOptions();
        checkLegacyScreenContext(minecraft);
        if (!ModList.get().isLoaded("jei") && !ModList.get().isLoaded("emi")
                && !ModList.get().isLoaded("roughlyenoughitems")) {
            throw new AssertionError("Recipe-viewer mixin audit requires JEI, EMI or REI");
        }

        try {
            var lookup = MethodHandles.lookup();
            if (ModList.get().isLoaded("jei")) {
                lookup.ensureInitialized(IngredientCommands.class);
                lookup.ensureInitialized(BookmarkOverlay.class);
                lookup.ensureInitialized(IngredientGrid.class);
                checkJeiInputLifecycle(minecraft);
            }
            if (ModList.get().isLoaded("emi")) {
                Class.forName("dev.emi.emi.screen.EmiScreenManager$ScreenSpace");
            }
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to initialize JEI mixin targets", e);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Unable to initialize EMI mixin targets", e);
        }

        MixinEnvironment.getCurrentEnvironment().audit();
        if (ModList.get().isLoaded("roughlyenoughitems")) {
            ReiKeyConversionClientProbe.run();
        }
        LOGGER.info("AE2TB recipe-viewer mixin client audit PASS");
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
        LOGGER.info("AE2TB ingredient amount config checks PASS");
    }

    private static void checkJeiInputLifecycle(Minecraft minecraft) {
        int[] actions = {0};
        boolean[] available = {true};
        boolean[] covered = {false};
        var area = new mezz.jei.common.util.ImmutableRect2i(0, 0, 16, 16);
        var scene = new GuiInputScene(
                InputArea.builder("Foreground", (x, y) -> covered[0]).blockUnhandledMouseInput().build(),
                InputArea.builder("Bookmark", area::contains)
                        .controls((screen, input, keys) -> Optional.of(InputAction.run(() -> actions[0]++)
                                .within(area::contains).when(() -> available[0])))
                        .build());
        var editor = new ITextInputHandler() {
            public boolean hasKeyboardFocus() { return false; }
            public void clearFocus() { }
            public boolean onCharTyped(char character, int modifiers) { return false; }
            public boolean isMouseOver(double x, double y) { return false; }
            public Optional<IInputInteraction> beginInput(Screen screen, UserInput input, IInternalKeyMappings keys) {
                return Optional.empty();
            }
        };
        var controller = new GuiInputController(scene, new InputCommands(), editor, Internal.getKeyMappings());
        var press = UserInput.fromVanilla(8, 8, 2, InputPhase.PRESS).orElseThrow();
        var release = UserInput.fromVanilla(8, 8, 2, InputPhase.RELEASE).orElseThrow();
        var screen = minecraft.screen;
        if (!controller.mousePressed(screen, press) || actions[0] != 0) {
            throw new AssertionError("JEI press must capture without executing");
        }
        controller.mousePressed(screen, press);
        controller.mouseReleased(screen, release);
        controller.mouseReleased(screen, release);
        if (actions[0] != 1) {
            throw new AssertionError("Repeated press/release must execute exactly once");
        }
        controller.mousePressed(screen, press);
        controller.mouseReleased(screen, release.withMousePosition(32, 32));
        controller.mousePressed(screen, press);
        available[0] = false;
        controller.mouseReleased(screen, release);
        available[0] = true;
        controller.mousePressed(screen, press);
        controller.screenChanged();
        controller.mouseReleased(screen, release);
        covered[0] = true;
        controller.mousePressed(screen, press);
        controller.mouseReleased(screen, release);
        if (actions[0] != 1) {
            throw new AssertionError("Outside, unavailable, cancelled, and covered clicks must not execute");
        }
        LOGGER.info("AE2TB JEI input lifecycle checks PASS");
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
