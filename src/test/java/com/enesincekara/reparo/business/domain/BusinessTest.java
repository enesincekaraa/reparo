package com.enesincekara.reparo.business.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BusinessTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-09-16T12:00:00Z");


    @Test
    void shouldRegisterBusiness() {
        Business business = Business.register(
                "Deniz Bilgisayar",
                CREATED_AT
        );

        assertAll(
                ()-> assertNotNull(business.id()),
                ()-> assertEquals("Deniz Bilgisayar",
                        business.name()),
                ()-> assertEquals(CREATED_AT, business.createdAt())
        );
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        Business business = Business.register(
                " \tDeniz Bilgisayar\n ",
                CREATED_AT
        );

        assertEquals("Deniz Bilgisayar", business.name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectMissingOrBlankName(String name) {
        assertThrows(
                IllegalArgumentException.class,
                () -> Business.register(name, CREATED_AT)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "🔧"})
    void shouldAcceptNameWith160CodePoints(String character) {
        String name = character.repeat(160);

        Business business = Business.register(
                name,
                CREATED_AT
        );

        assertEquals(name, business.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "🔧"})
    void shouldRejectNameWithMoreThan160CodePoints(
            String character
    ) {
        String name = character.repeat(161);

        assertThrows(
                IllegalArgumentException.class,
                () -> Business.register(name, CREATED_AT)
        );
    }

    @Test
    void shouldRejectMissingCreationTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Business.register(
                        "Deniz Bilgisayar",
                        null
                )
        );
    }

    @Test
    void shouldRestoreExistingBusiness() {
        UUID id = UUID.fromString(
                "1f792c6d-ec07-4379-9824-711b37c2e064"
        );

        Business business = Business.restore(
                id,
                "Deniz Bilgisayar",
                CREATED_AT
        );


        assertAll(
                ()-> assertEquals(id,business.id()),
                () -> assertEquals(
                        "Deniz Bilgisayar",
                        business.name()
                ),
                ()-> assertEquals(CREATED_AT,business.createdAt())
        );
    }


    @Test
    void shouldRejectRestoringBusinessWithoutId(){
        assertThrows(
                IllegalArgumentException.class,
                () -> Business.restore(
                        null,
                        "Deniz Bilgisayar",
                        CREATED_AT
                )
        );
    }

}