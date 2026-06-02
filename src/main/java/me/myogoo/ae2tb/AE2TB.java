package me.myogoo.ae2tb;

import me.myogoo.ae2tb.init.AE2TBConfig;
import me.myogoo.ae2tb.init.AE2TBItems;
import me.myogoo.ae2tb.init.AE2TBNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AE2TB.MODID)
public class AE2TB {
    public static final String MODID = "ae2tb";

    public AE2TB() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AE2TBConfig.initialize();
        AE2TBItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        AE2TBNetwork.init();
    }

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }
}
