package me.myogoo.ae2tb.init;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class AE2TBConfig {
    public static void initialize(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, me.myogoo.ae2tb.config.AE2TBConfig.COMMON_SPEC,
                "ae2tb-common.toml");
        container.registerConfig(ModConfig.Type.CLIENT, me.myogoo.ae2tb.config.AE2TBConfig.CLIENT_SPEC, "ae2tb-client.toml");
    }
}
