package me.myogoo.ae2tb.init;

import me.myogoo.ae2tb.AE2TB;
import me.myogoo.ae2tb.network.serverbound.AE2TBInteractionPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class AE2TBNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            AE2TB.makeId("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(id++, AE2TBInteractionPacket.class, AE2TBInteractionPacket::write,
                AE2TBInteractionPacket::decode, AE2TBInteractionPacket::handleOnServer);
    }
}
