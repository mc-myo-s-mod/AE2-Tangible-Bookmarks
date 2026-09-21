package me.myogoo.ae2tb.client;

import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.init.AE2TBConfigTab;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.config.MyoConfigTab;
import me.myogoo.myotus.client.gui.MyoIcon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = AE2TB.MODID, dist = Dist.CLIENT)
public final class AE2TBClient {
    public AE2TBClient(IEventBus modEventBus) {
        modEventBus.addListener(AE2TBClient::clientSetup);
        modEventBus.addListener(AE2TBClient::registerKeyMappings);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MyotusAPI.configTabs().registerTerminalConfigTab(new MyoConfigTab(
                AE2TB.makeId("terminal_bookmarks"),
                TranslateKey.CATEGORY.getTranslate(),
                MyoIcon.AE2TB_CONFIG,
                "ae2tb.json",
                new AE2TBConfigTab()
        )));
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.PICKUP_SINGLE_ITEM);
        event.register(KeyBindings.PICKUP_SET_ITEM);
        event.register(KeyBindings.PICKED_ITEM_AUTOCRAFTING);
    }
}
