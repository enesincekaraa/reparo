package com.enesincekara.reparo.customer.domain;

public final class InvalidCustomerNameException
        extends IllegalArgumentException {

    public InvalidCustomerNameException(String message) {
        super(message);
    }
}