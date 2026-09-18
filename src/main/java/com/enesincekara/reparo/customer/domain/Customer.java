package com.enesincekara.reparo.customer.domain;

import java.time.Instant;
import java.util.UUID;

public final class Customer {

    private static final int MAX_NAME_LENGTH = 160;

    private final UUID id;
    private final UUID businessId;
    private final String fullName;
    private final PhoneNumber phoneNumber;
    private final Instant createdAt;

    private Customer(
            UUID id,
            UUID businessId,
            String fullName,
            PhoneNumber phoneNumber,
            Instant createdAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Customer id must not be null"
            );
        }

        if (businessId == null) {
            throw new IllegalArgumentException(
                    "Business id must not be null"
            );
        }

        if (phoneNumber == null) {
            throw new IllegalArgumentException(
                    "Phone number must not be null"
            );
        }

        if (createdAt == null) {
            throw new IllegalArgumentException(
                    "Customer creation time must not be null"
            );
        }

        this.id = id;
        this.businessId = businessId;
        this.fullName = normalizeFullName(fullName);
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    public static Customer register(
            UUID businessId,
            String fullName,
            PhoneNumber phoneNumber,
            Instant createdAt
    ) {
        return new Customer(
                UUID.randomUUID(),
                businessId,
                fullName,
                phoneNumber,
                createdAt
        );
    }

    public static Customer restore(
            UUID id,
            UUID businessId,
            String fullName,
            PhoneNumber phoneNumber,
            Instant createdAt
    ) {
        return new Customer(
                id,
                businessId,
                fullName,
                phoneNumber,
                createdAt
        );
    }

    private static String normalizeFullName(String fullName) {
        if (fullName == null) {
            throw new InvalidCustomerNameException(
                    "Customer name must not be null"
            );
        }

        String normalizedName = fullName.strip();

        if (normalizedName.isEmpty()) {
            throw new InvalidCustomerNameException(
                    "Customer name must not be blank"
            );
        }

        int nameLength = normalizedName.codePointCount(
                0,
                normalizedName.length()
        );

        if (nameLength > MAX_NAME_LENGTH) {
            throw new InvalidCustomerNameException(
                    "Customer name must not exceed 160 code points"
            );
        }

        return normalizedName;
    }

    public UUID id() {
        return id;
    }

    public UUID businessId() {
        return businessId;
    }

    public String fullName() {
        return fullName;
    }

    public PhoneNumber phoneNumber() {
        return phoneNumber;
    }

    public Instant createdAt() {
        return createdAt;
    }
}