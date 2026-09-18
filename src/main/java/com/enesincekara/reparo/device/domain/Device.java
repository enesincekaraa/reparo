package com.enesincekara.reparo.device.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Device {

    private static final int MAX_BRAND_LENGTH = 80;
    private static final int MAX_MODEL_LENGTH = 120;
    private static final int MAX_SERIAL_NUMBER_LENGTH = 100;

    private final UUID id;
    private final UUID businessId;
    private final UUID customerId;
    private final DeviceType type;
    private final String brand;
    private final String model;
    private final String serialNumber;
    private final Instant createdAt;

    private Device(
            UUID id,
            UUID businessId,
            UUID customerId,
            DeviceType type,
            String brand,
            String model,
            String serialNumber,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "Device id must not be null");
        this.businessId = Objects.requireNonNull(
                businessId,
                "Business id must not be null"
        );
        this.customerId = Objects.requireNonNull(
                customerId,
                "Customer id must not be null"
        );
        this.type = validateType(type);
        this.brand = normalizeRequired(
                brand,
                "brand",
                "Device brand",
                MAX_BRAND_LENGTH
        );
        this.model = normalizeOptional(
                model,
                "model",
                "Device model",
                MAX_MODEL_LENGTH
        );
        this.serialNumber = normalizeOptional(
                serialNumber,
                "serialNumber",
                "Device serial number",
                MAX_SERIAL_NUMBER_LENGTH
        );
        this.createdAt = Objects.requireNonNull(
                createdAt,
                "Created time must not be null"
        );
    }

    public static Device register(
            UUID businessId,
            UUID customerId,
            DeviceType type,
            String brand,
            String model,
            String serialNumber,
            Instant createdAt
    ) {
        return new Device(
                UUID.randomUUID(),
                businessId,
                customerId,
                type,
                brand,
                model,
                serialNumber,
                createdAt
        );
    }

    public static Device restore(
            UUID id,
            UUID businessId,
            UUID customerId,
            DeviceType type,
            String brand,
            String model,
            String serialNumber,
            Instant createdAt
    ) {
        return new Device(
                id,
                businessId,
                customerId,
                type,
                brand,
                model,
                serialNumber,
                createdAt
        );
    }

    private static DeviceType validateType(DeviceType type) {
        if (type == null) {
            throw new InvalidDeviceException(
                    "type",
                    "Device type must not be null"
            );
        }

        return type;
    }

    private static String normalizeRequired(
            String value,
            String field,
            String label,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new InvalidDeviceException(
                    field,
                    label + " must not be blank"
            );
        }

        String normalized = value.strip();
        validateLength(normalized, field, label, maximumLength);

        return normalized;
    }

    private static String normalizeOptional(
            String value,
            String field,
            String label,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.strip();
        validateLength(normalized, field, label, maximumLength);

        return normalized;
    }

    private static void validateLength(
            String value,
            String field,
            String label,
            int maximumLength
    ) {
        int codePointCount = value.codePointCount(0, value.length());

        if (codePointCount > maximumLength) {
            throw new InvalidDeviceException(
                    field,
                    label + " must not exceed "
                            + maximumLength
                            + " code points"
            );
        }
    }

    public UUID id() {
        return id;
    }

    public UUID businessId() {
        return businessId;
    }

    public UUID customerId() {
        return customerId;
    }

    public DeviceType type() {
        return type;
    }

    public String brand() {
        return brand;
    }

    public String model() {
        return model;
    }

    public String serialNumber() {
        return serialNumber;
    }

    public Instant createdAt() {
        return createdAt;
    }
}