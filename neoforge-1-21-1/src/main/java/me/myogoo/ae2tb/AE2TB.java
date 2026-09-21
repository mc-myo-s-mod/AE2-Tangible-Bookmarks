package me.myogoo.ae2tb;

import me.myogoo.ae2tb.client.AE2TBClient;
import me.myogoo.ae2tb.init.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.ae2tb.init.AE2TBNetwork;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(AE2TB.MODID)
public class AE2TB {
    public static final String MODID = "ae2tb";

    public AE2TB(IEventBus modEventBus, ModContainer modContainer) {
        AE2TBConfig.initialize(modContainer);
        AE2TBItems.REGISTER.register(modEventBus);
        modEventBus.addListener(AE2TBNetwork::init);
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

}
