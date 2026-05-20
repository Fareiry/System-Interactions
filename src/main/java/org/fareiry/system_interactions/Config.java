package org.fareiry.system_interactions;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = SystemInteractions.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_DIALOGS = BUILDER
            .comment("Allows this client to show system MessageBox dialogs requested by a server command.")
            .define("enableSystemDialogs", true);

    private static final ModConfigSpec.BooleanValue ENABLE_TOASTS = BUILDER
            .comment("Allows this client to show OS notification/toast effects requested by a server command.")
            .define("enableSystemToasts", true);

    private static final ModConfigSpec.IntValue MAX_TITLE_LENGTH = BUILDER
            .comment("Maximum accepted title length for system interactions. Longer titles are truncated client-side and server-side.")
            .defineInRange("maxTitleLength", 120, 1, 512);

    private static final ModConfigSpec.IntValue MAX_MESSAGE_LENGTH = BUILDER
            .comment("Maximum accepted message length for system interactions. Longer messages are truncated client-side and server-side.")
            .defineInRange("maxMessageLength", 1000, 1, 4096);

    private static final ModConfigSpec.IntValue DIALOG_COOLDOWN_MS = BUILDER
            .comment("Client-side cooldown between system dialogs, in milliseconds. Prevents accidental message-box spam.")
            .defineInRange("dialogCooldownMs", 1000, 0, 60000);

    private static final ModConfigSpec.IntValue TOAST_COOLDOWN_MS = BUILDER
            .comment("Client-side cooldown between toast/notification effects, in milliseconds.")
            .defineInRange("toastCooldownMs", 500, 0, 60000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableSystemDialogs;
    public static boolean enableSystemToasts;
    public static int maxTitleLength;
    public static int maxMessageLength;
    public static int dialogCooldownMs;
    public static int toastCooldownMs;

    private Config() {
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        enableSystemDialogs = ENABLE_DIALOGS.get();
        enableSystemToasts = ENABLE_TOASTS.get();
        maxTitleLength = MAX_TITLE_LENGTH.get();
        maxMessageLength = MAX_MESSAGE_LENGTH.get();
        dialogCooldownMs = DIALOG_COOLDOWN_MS.get();
        toastCooldownMs = TOAST_COOLDOWN_MS.get();
    }
}
