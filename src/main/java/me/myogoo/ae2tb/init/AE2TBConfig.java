package me.myogoo.ae2tb.init;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class AE2TBConfig {
    private AE2TBConfig() {
    }

    public static void initialize() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, me.myogoo.ae2tb.config.AE2TBConfig.COMMON_SPEC,
                "ae2tb-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, me.myogoo.ae2tb.config.AE2TBConfig.CLIENT_SPEC,
                "ae2tb-client.toml");
    }
}
