package me.myogoo.ae2tb.gametest;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("ae2tb_client_probe")
public final class Ae2tbGameTestMod {
    public Ae2tbGameTestMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Ae2tbGameTestMod::register);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> RecipeViewerClientProbe::new);
    }

    private static void register(RegisterGameTestsEvent event) {
        event.register(Ae2tbInteractionGameTests.class);
    }
}
