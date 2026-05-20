package org.fareiry.system_interactions.util;

import org.fareiry.system_interactions.Config;

public final class SystemInteractionSanitizer {
    private SystemInteractionSanitizer() {
    }

    public static String sanitizeTitle(String title) {
        return sanitize(title, Config.maxTitleLength <= 0 ? 120 : Config.maxTitleLength);
    }

    public static String sanitizeMessage(String message) {
        return sanitize(message, Config.maxMessageLength <= 0 ? 1000 : Config.maxMessageLength);
    }

    private static String sanitize(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        String cleaned = text
                .replace('\u0000', ' ')
                .replace('\r', ' ');

        if (cleaned.length() <= maxLength) {
            return cleaned;
        }

        return cleaned.substring(0, Math.max(0, maxLength));
    }
}
