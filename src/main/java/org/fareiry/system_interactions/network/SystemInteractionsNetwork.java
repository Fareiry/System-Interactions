package org.fareiry.system_interactions.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.fareiry.system_interactions.util.SystemInteractionIcon;
import org.fareiry.system_interactions.util.SystemInteractionSanitizer;

import java.util.Collection;

public final class SystemInteractionsNetwork {
    private SystemInteractionsNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SystemDialogPayload.TYPE, SystemDialogPayload.STREAM_CODEC, SystemInteractionsClientPayloadHandler::handleSystemDialog);
        registrar.playToClient(SystemToastPayload.TYPE, SystemToastPayload.STREAM_CODEC, SystemInteractionsClientPayloadHandler::handleSystemToast);
    }

    public static void sendDialog(Collection<ServerPlayer> targets, SystemInteractionIcon icon, String title, String message) {
        SystemDialogPayload payload = new SystemDialogPayload(
                SystemInteractionSanitizer.sanitizeTitle(title),
                SystemInteractionSanitizer.sanitizeMessage(message),
                icon.id()
        );

        for (ServerPlayer player : targets) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    public static void sendToast(Collection<ServerPlayer> targets, SystemInteractionIcon icon, String title, String message) {
        SystemToastPayload payload = new SystemToastPayload(
                SystemInteractionSanitizer.sanitizeTitle(title),
                SystemInteractionSanitizer.sanitizeMessage(message),
                icon.id()
        );

        for (ServerPlayer player : targets) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }
}
