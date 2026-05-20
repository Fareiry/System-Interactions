package org.fareiry.system_interactions.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.fareiry.system_interactions.SystemInteractions;

public record SystemToastPayload(String title, String message, String icon) implements CustomPacketPayload {
    public static final Type<SystemToastPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SystemInteractions.MODID, "system_toast")
    );

    public static final StreamCodec<ByteBuf, SystemToastPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SystemToastPayload::title,
            ByteBufCodecs.STRING_UTF8,
            SystemToastPayload::message,
            ByteBufCodecs.STRING_UTF8,
            SystemToastPayload::icon,
            SystemToastPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
