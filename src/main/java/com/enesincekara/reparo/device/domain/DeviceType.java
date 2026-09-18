package com.enesincekara.reparo.device.domain;

import java.util.Locale;

public enum DeviceType {
    PHONE,
    TABLET,
    LAPTOP,
    DESKTOP,
    GAME_CONSOLE,
    OTHER;

    public static DeviceType from(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidDeviceException(
                    "type",
                    "Device type must not be blank"
            );
        }

        String normalized = value.strip()
                .toUpperCase(Locale.ROOT);

        try {
            return DeviceType.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            throw new InvalidDeviceException(
                    "type",
                    "Unsupported device type: " + value
            );
        }
    }
}