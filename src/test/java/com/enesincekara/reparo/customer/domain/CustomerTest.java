package com.enesincekara.reparo.customer.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private static final UUID BUSINESS_ID =
            UUID.fromString(
                    "ba530d61-63cb-4c8c-8c18-8bc39fb61f2f"
            );

    private static final Instant CREATED_AT =
            Instant.parse("2026-09-17T13:00:00Z");

    private static final PhoneNumber PHONE_NUMBER =
            PhoneNumber.of("+905321234567");

    @Test
    void shouldRegisterCustomer() {
        Customer customer = Customer.register(
                BUSINESS_ID,
                "Ayşe Yılmaz",
                PHONE_NUMBER,
                CREATED_AT
        );

        assertAll(
                () -> assertNotNull(customer.id()),
                () -> assertEquals(
                        BUSINESS_ID,
                        customer.businessId()
                ),
                () -> assertEquals(
                        "Ayşe Yılmaz",
                        customer.fullName()
                ),
                () -> assertEquals(
                        PHONE_NUMBER,
                        customer.phoneNumber()
                ),
                () -> assertEquals(
                        CREATED_AT,
                        customer.createdAt()
                )
        );
    }

    @Test
    void shouldRemoveSurroundingWhitespaceFromName() {
        Customer customer = Customer.register(
                BUSINESS_ID,
                " \tAyşe Yılmaz\n ",
                PHONE_NUMBER,
                CREATED_AT
        );

        assertEquals("Ayşe Yılmaz", customer.fullName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectMissingOrBlankName(String fullName) {
        assertThrows(
                InvalidCustomerNameException.class,
                () -> Customer.register(
                        BUSINESS_ID,
                        fullName,
                        PHONE_NUMBER,
                        CREATED_AT
                )
        );
    }

    @Test
    void shouldRejectNameLongerThan160CodePoints() {
        String fullName = "A".repeat(161);

        assertThrows(
                InvalidCustomerNameException.class,
                () -> Customer.register(
                        BUSINESS_ID,
                        fullName,
                        PHONE_NUMBER,
                        CREATED_AT
                )
        );
    }

    @Test
    void shouldRejectMissingBusinessId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Customer.register(
                        null,
                        "Ayşe Yılmaz",
                        PHONE_NUMBER,
                        CREATED_AT
                )
        );
    }

    @Test
    void shouldRejectMissingPhoneNumber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Customer.register(
                        BUSINESS_ID,
                        "Ayşe Yılmaz",
                        null,
                        CREATED_AT
                )
        );
    }

    @Test
    void shouldRejectMissingCreationTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Customer.register(
                        BUSINESS_ID,
                        "Ayşe Yılmaz",
                        PHONE_NUMBER,
                        null
                )
        );
    }

    @Test
    void shouldRestoreExistingCustomer() {
        UUID customerId = UUID.fromString(
                "3e38ca82-a470-4f2f-aae8-dbbd8715d673"
        );

        Customer customer = Customer.restore(
                customerId,
                BUSINESS_ID,
                "Ayşe Yılmaz",
                PHONE_NUMBER,
                CREATED_AT
        );

        assertAll(
                () -> assertEquals(
                        customerId,
                        customer.id()
                ),
                () -> assertEquals(
                        BUSINESS_ID,
                        customer.businessId()
                ),
                () -> assertEquals(
                        PHONE_NUMBER,
                        customer.phoneNumber()
                )
        );
    }
}