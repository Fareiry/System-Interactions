package org.fareiry.system_interactions.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.fareiry.system_interactions.Config;
import org.fareiry.system_interactions.util.SystemInteractionIcon;
import org.fareiry.system_interactions.util.SystemInteractionSanitizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Locale;

public final class SystemInteractionsClientPayloadHandler {
    private static long lastDialogTimeMs = 0L;
    private static long lastToastTimeMs = 0L;

    private SystemInteractionsClientPayloadHandler() {
    }

    public static void handleSystemDialog(SystemDialogPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> showSystemDialog(payload.title(), payload.message(), payload.icon()));
    }

    public static void handleSystemToast(SystemToastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> showSystemToast(payload.title(), payload.message(), payload.icon()));
    }

    private static void showSystemDialog(String title, String message, String icon) {
        if (!Config.enableSystemDialogs || isOnCooldown(true)) {
            return;
        }

        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String safeTitle = SystemInteractionSanitizer.sanitizeTitle(title);
        String safeMessage = SystemInteractionSanitizer.sanitizeMessage(message);
        String normalizedIcon = SystemInteractionIcon.byId(icon).platformName();

        try {
            if (osName.contains("win")) {
                showWindowsMessageBox(safeTitle, safeMessage, normalizedIcon);
            } else {
                showGenericUnixDialog(safeTitle, safeMessage, normalizedIcon);
            }

            lastDialogTimeMs = System.currentTimeMillis();
        } catch (IOException exception) {
            System.err.println("[System Interactions] Failed to open system dialog: " + exception.getMessage());
        }
    }

    private static void showWindowsMessageBox(String title, String message, String icon) throws IOException {
        String encodedTitle = Base64.getEncoder().encodeToString(title.getBytes(StandardCharsets.UTF_8));
        String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
        String encodedIcon = Base64.getEncoder().encodeToString(icon.getBytes(StandardCharsets.UTF_8));

        String script = "Add-Type -AssemblyName PresentationFramework\n"
                + "$title = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String('" + encodedTitle + "'))\n"
                + "$message = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String('" + encodedMessage + "'))\n"
                + "$icon = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String('" + encodedIcon + "'))\n"
                + "[System.Windows.MessageBox]::Show($message, $title, 'OK', $icon) | Out-Null\n";

        String encodedCommand = Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));

        new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-ExecutionPolicy", "Bypass",
                "-EncodedCommand", encodedCommand
        ).start();
    }

    private static void showSystemToast(String title, String message, String icon) {
        if (!Config.enableSystemToasts || isOnCooldown(false)) {
            return;
        }

        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String safeTitle = SystemInteractionSanitizer.sanitizeTitle(title);
        String safeMessage = SystemInteractionSanitizer.sanitizeMessage(message);
        String normalizedIcon = SystemInteractionIcon.byId(icon).platformName();

        try {
            if (osName.contains("win")) {
                showWindowsToast(safeTitle, safeMessage, normalizedIcon);
            } else {
                showGenericUnixToast(safeTitle, safeMessage, normalizedIcon);
            }

            lastToastTimeMs = System.currentTimeMillis();
        } catch (IOException exception) {
            System.err.println("[System Interactions] Failed to open system toast: " + exception.getMessage());
        }
    }

    private static boolean isOnCooldown(boolean dialog) {
        long now = System.currentTimeMillis();
        long last = dialog ? lastDialogTimeMs : lastToastTimeMs;
        int cooldown = dialog ? Config.dialogCooldownMs : Config.toastCooldownMs;
        return cooldown > 0 && now - last < cooldown;
    }

    private static void showWindowsToast(String title, String message, String icon) throws IOException {
        String encodedTitle = Base64.getEncoder().encodeToString(title.getBytes(StandardCharsets.UTF_8));
        String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
        String encodedIcon = Base64.getEncoder().encodeToString(icon.getBytes(StandardCharsets.UTF_8));

        Path helperPath = extractWindowsToastHelper();

        ProcessBuilder builder = new ProcessBuilder(
                helperPath.toAbsolutePath().toString(),
                encodedTitle,
                encodedMessage,
                encodedIcon
        );
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        builder.start();
    }

    private static Path extractWindowsToastHelper() throws IOException {
        // The toast helper is shipped as a prebuilt resource inside the mod jar.
        // We only extract the already-built executable; no C# source is compiled
        // and no PowerShell code is generated at runtime.
        String resourcePath = "/assets/system_interactions/native/windows/MinecraftToastHelper.exe";
        Path helperDir = Path.of(System.getProperty("java.io.tmpdir"), "SystemInteractionsToastHelper");
        Path helperPath = helperDir.resolve("MinecraftToastHelper.exe");
        Path markerPath = helperDir.resolve("version.txt");
        String expectedVersion = "1";

        if (Files.exists(helperPath) && Files.exists(markerPath)) {
            String currentVersion = Files.readString(markerPath, StandardCharsets.UTF_8).trim();
            if (expectedVersion.equals(currentVersion)) {
                return helperPath;
            }
        }

        Files.createDirectories(helperDir);

        try (InputStream stream = SystemInteractionsClientPayloadHandler.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IOException("Missing bundled Windows toast helper resource: " + resourcePath);
            }

            Path tempPath = helperDir.resolve("MinecraftToastHelper.exe.tmp");
            Files.copy(stream, tempPath, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tempPath, helperPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }

        Files.writeString(markerPath, expectedVersion, StandardCharsets.UTF_8);
        return helperPath;
    }

    private static void showGenericUnixDialog(String title, String message, String icon) throws IOException {
        String zenityMode = icon.equals("Error") ? "--error" : icon.equals("Warning") ? "--warning" : "--info";

        new ProcessBuilder(
                "sh",
                "-c",
                "command -v zenity >/dev/null 2>&1 && zenity $2 --title=\"$0\" --text=\"$1\" || "
                        + "command -v xmessage >/dev/null 2>&1 && xmessage -title \"$0\" \"$1\"",
                title,
                message,
                zenityMode
        ).start();
    }

    private static void showGenericUnixToast(String title, String message, String icon) throws IOException {
        String urgency = icon.equals("Error") ? "critical" : icon.equals("Warning") ? "normal" : "low";

        new ProcessBuilder(
                "sh",
                "-c",
                "command -v notify-send >/dev/null 2>&1 && notify-send -u $2 \"$0\" \"$1\" || "
                        + "command -v zenity >/dev/null 2>&1 && zenity --notification --text=\"$0: $1\"",
                title,
                message,
                urgency
        ).start();
    }
}
