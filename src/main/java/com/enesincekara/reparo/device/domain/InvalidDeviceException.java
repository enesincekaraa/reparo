package com.enesincekara.reparo.device.domain;

public final class InvalidDeviceException extends RuntimeException {

    private final String field;

    public InvalidDeviceException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}