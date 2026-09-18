package com.enesincekara.reparo.customer.api;

import com.enesincekara.reparo.customer.domain.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        UUID businessId,
        String fullName,
        String phoneNumber,
        Instant createdAt
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.id(),
                customer.businessId(),
                customer.fullName(),
                customer.phoneNumber().value(),
                customer.createdAt()
        );
    }
}
