package com.enesincekara.reparo.device.application;

import java.util.UUID;

public final class DeviceNotFoundException extends RuntimeException {

    private final UUID businessId;
    private final UUID customerId;
    private final UUID deviceId;

    public DeviceNotFoundException(
            UUID businessId,
            UUID customerId,
            UUID deviceId
    ) {
        super(
                "Device does not exist in requested customer scope: "
                        + deviceId
        );
        this.businessId = businessId;
        this.customerId = customerId;
        this.deviceId = deviceId;
    }

    public UUID businessId() {
        return businessId;
    }

    public UUID customerId() {
        return customerId;
    }

    public UUID deviceId() {
        return deviceId;
    }
}