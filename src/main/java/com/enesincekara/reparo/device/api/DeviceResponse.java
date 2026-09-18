package com.enesincekara.reparo.device.api;

import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;

import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        UUID businessId,
        UUID customerId,
        DeviceType type,
        String brand,
        String model,
        String serialNumber,
        Instant createdAt
) {

    public static DeviceResponse from(Device device) {
        return new DeviceResponse(
                device.id(),
                device.businessId(),
                device.customerId(),
                device.type(),
                device.brand(),
                device.model(),
                device.serialNumber(),
                device.createdAt()
        );
    }
}