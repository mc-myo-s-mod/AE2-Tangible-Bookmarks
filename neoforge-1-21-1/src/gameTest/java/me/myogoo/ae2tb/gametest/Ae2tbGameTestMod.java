package me.myogoo.ae2tb.gametest;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

@Mod("ae2tb_gametest")
public final class Ae2tbGameTestMod {
    public Ae2tbGameTestMod(IEventBus modEventBus) {
        modEventBus.addListener(Ae2tbGameTestMod::register);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new Ae2tbJeiMixinClientAudit();
        }
    }

    private static void register(RegisterGameTestsEvent event) {
        event.register(Ae2tbInteractionGameTests.class);
    }
}
