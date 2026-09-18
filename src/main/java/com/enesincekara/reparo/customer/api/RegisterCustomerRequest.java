package com.enesincekara.reparo.customer.api;

public record RegisterCustomerRequest(
        String fullName,
        String phoneNumber
) {
}
