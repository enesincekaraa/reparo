package com.enesincekara.reparo.business.application;

import java.util.UUID;

public final class BusinessNotFoundException
        extends RuntimeException {

    private final UUID businessId;

    public BusinessNotFoundException(UUID businessId) {
        super("Business does not exist with id: " + businessId);
        this.businessId = businessId;
    }

    public UUID businessId() {
        return businessId;
    }
}