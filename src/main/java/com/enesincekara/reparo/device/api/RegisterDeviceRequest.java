package com.enesincekara.reparo.device.api;

public record RegisterDeviceRequest(
        String type,
        String brand,
        String model,
        String serialNumber
) {
}