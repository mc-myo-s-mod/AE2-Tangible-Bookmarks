package me.myogoo.ae2tb.init;

import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.client.KeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = AE2TB.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AE2TBKeyBinding {
    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.PICKUP_SINGLE_ITEM);
        event.register(KeyBindings.PICKUP_SET_ITEM);
        event.register(KeyBindings.PICKED_ITEM_AUTOCRAFTING);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(AE2TBConfigTab::initialize);
    }
}
