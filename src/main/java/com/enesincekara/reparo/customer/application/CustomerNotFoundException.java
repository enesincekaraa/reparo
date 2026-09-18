package com.enesincekara.reparo.customer.application;

import java.util.UUID;

public final class CustomerNotFoundException
        extends RuntimeException {

    private final UUID businessId;
    private final UUID customerId;

    public CustomerNotFoundException(
            UUID businessId,
            UUID customerId
    ) {
        super(
                "Customer does not exist in business: "
                        + customerId
        );

        this.businessId = businessId;
        this.customerId = customerId;
    }

    public UUID businessId() {
        return businessId;
    }

    public UUID customerId() {
        return customerId;
    }
}