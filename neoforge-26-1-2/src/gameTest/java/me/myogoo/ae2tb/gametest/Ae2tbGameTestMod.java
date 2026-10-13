package me.myogoo.ae2tb.gametest;

import me.myogoo.ae2tb.AE2TB;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@Mod("ae2tb_gametest")
public final class Ae2tbGameTestMod {
    private static final ResourceKey<Consumer<GameTestHelper>> TEST_FUNCTION =
            ResourceKey.create(Registries.TEST_FUNCTION, AE2TB.makeId("stale_menu_requests_are_rejected"));

    public Ae2tbGameTestMod(IEventBus modEventBus) {
        modEventBus.addListener(Ae2tbGameTestMod::registerFunction);
        modEventBus.addListener(Ae2tbGameTestMod::registerTest);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            new Ae2tbJeiMixinClientAudit();
        }
    }

    private static void registerFunction(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.TEST_FUNCTION) {
            event.register(Registries.TEST_FUNCTION, TEST_FUNCTION.identifier(),
                    () -> Ae2tbInteractionGameTests::staleMenuRequestsAreRejected);
        }
    }

    private static void registerTest(RegisterGameTestsEvent event) {
        event.registerTest(TEST_FUNCTION.identifier(), new FunctionGameTestInstance(TEST_FUNCTION, new TestData<>(
                Holder.direct(new TestEnvironmentDefinition.AllOf()), AE2TB.makeId("empty"),
                20, 0, true, Rotation.NONE, false, 1, 1, false, 0)));
    }
}
