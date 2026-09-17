package com.enesincekara.reparo.business.domain;

public class InvalidBusinessNameException extends IllegalArgumentException {
    public InvalidBusinessNameException(String message) {
        super(message);
    }
}
