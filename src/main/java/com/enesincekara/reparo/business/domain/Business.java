package com.enesincekara.reparo.business.domain;


import java.time.Instant;
import java.util.UUID;

public final class Business {

    private static final int MAX_NAME_LENGTH = 160;

    private final UUID id;
    private final String name;
    private final Instant createdAt;

    private Business(UUID id, String name, Instant createdAt) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Business id must not be null"
            );
        }
        this.id = id;
        this.name = normalizeName(name);
        if (createdAt == null) {
            throw new IllegalArgumentException(
                    "Business creation time must not be null"
            );
        }
        this.createdAt = createdAt;
    }


    public static Business register(String name,Instant createdAt) {
        return new Business(UUID.randomUUID(), name, createdAt);
    }

    public static Business restore(
            UUID id,
            String name,
            Instant createdAt
    ){
        return new Business(id, name, createdAt);
    }

    private static String normalizeName(String name) {
        if (name == null) {
            throw new InvalidBusinessNameException(
                    "Business name must not be null"
            );
        }
        String normalizedName = name.strip();

        if (normalizedName.isEmpty()){
            throw new InvalidBusinessNameException(
                    "Business name must not be blank"
            );
        }

        int nameLength = normalizedName.codePointCount(0, normalizedName.length());

        if (nameLength > MAX_NAME_LENGTH) {
            throw new InvalidBusinessNameException(
                    "Business name must not exceed 160 code points"
            );
        }
        return normalizedName;

    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
