package org.fareiry.system_interactions.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.fareiry.system_interactions.SystemInteractions;

public record SystemDialogPayload(String title, String message, String icon) implements CustomPacketPayload {
    public static final Type<SystemDialogPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SystemInteractions.MODID, "system_dialog")
    );

    public static final StreamCodec<ByteBuf, SystemDialogPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SystemDialogPayload::title,
            ByteBufCodecs.STRING_UTF8,
            SystemDialogPayload::message,
            ByteBufCodecs.STRING_UTF8,
            SystemDialogPayload::icon,
            SystemDialogPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
