package org.fareiry.system_interactions.util;

import java.util.Locale;

public enum SystemInteractionIcon {
    INFO("info", "Information"),
    WARNING("warning", "Warning"),
    ERROR("error", "Error");

    private final String id;
    private final String platformName;

    SystemInteractionIcon(String id, String platformName) {
        this.id = id;
        this.platformName = platformName;
    }

    public String id() {
        return id;
    }

    public String platformName() {
        return platformName;
    }

    public static SystemInteractionIcon byId(String id) {
        if (id == null) {
            return INFO;
        }

        return switch (id.toLowerCase(Locale.ROOT)) {
            case "error" -> ERROR;
            case "warning" -> WARNING;
            default -> INFO;
        };
    }
}
