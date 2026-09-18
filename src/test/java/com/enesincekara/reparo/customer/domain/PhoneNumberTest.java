package com.enesincekara.reparo.customer.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "+905321234567",
            "+12025550123",
            "+442071838750"
    })
    void shouldCreatePhoneNumber(String value) {
        PhoneNumber phoneNumber = PhoneNumber.of(value);

        assertEquals(value, phoneNumber.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "905321234567",
            "+012345678",
            "+1234567",
            "+1234567890123456",
            "+90 532 123 45 67"
    })
    void shouldRejectInvalidPhoneNumber(String value) {
        assertThrows(
                InvalidPhoneNumberException.class,
                () -> PhoneNumber.of(value)
        );
    }
}