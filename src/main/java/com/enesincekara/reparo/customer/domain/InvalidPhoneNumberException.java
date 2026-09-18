package com.enesincekara.reparo.customer.domain;

public class InvalidPhoneNumberException extends IllegalArgumentException {
    public InvalidPhoneNumberException(String message) {
        super(message);
    }
}
