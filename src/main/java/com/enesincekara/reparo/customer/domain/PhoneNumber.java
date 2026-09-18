package com.enesincekara.reparo.customer.domain;

import java.util.regex.Pattern;

public record PhoneNumber(String value) {

    private static final Pattern E164_PATTERN =
            Pattern.compile("^\\+[1-9]\\d{7,14}$");

    public PhoneNumber{
        if (value==null) {
            throw new InvalidPhoneNumberException(
                    "Phone number must not be null"
            );
        }

        String normalizedValue=value.strip();


        if (!E164_PATTERN.matcher(normalizedValue).matches()) {
            throw new InvalidPhoneNumberException(
                    "Phone number must be in E.164 format"
            );
        }

        value = normalizedValue;
    }

    public static PhoneNumber of(String value){
        return new PhoneNumber(value);
    }
}
