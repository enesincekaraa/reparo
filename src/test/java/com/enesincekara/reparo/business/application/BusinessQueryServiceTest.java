package com.enesincekara.reparo.business.application;

import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessEntity;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessQueryServiceTest {

    @Mock
    private BusinessJpaRepository repository;

    @Test
    void shouldReturnBusinessWhenItExists() {
        UUID id = UUID.fromString(
                "1f792c6d-ec07-4379-9824-711b37c2e064"
        );

        Instant createdAt =
                Instant.parse("2026-09-17T10:00:00Z");

        Business business = Business.restore(
                id,
                "Deniz Bilgisayar",
                createdAt
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(
                        BusinessEntity.from(business)
                ));

        BusinessQueryService service =
                new BusinessQueryService(repository);

        Business result = service.get(id);

        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals(
                        "Deniz Bilgisayar",
                        result.name()
                ),
                () -> assertEquals(
                        createdAt,
                        result.createdAt()
                )
        );

        verify(repository).findById(id);
    }

    @Test
    void shouldThrowWhenBusinessDoesNotExist() {
        UUID id = UUID.fromString(
                "e5d73ac0-36c6-472a-9033-5ce015d3db98"
        );

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        BusinessQueryService service =
                new BusinessQueryService(repository);

        BusinessNotFoundException exception =
                assertThrows(
                        BusinessNotFoundException.class,
                        () -> service.get(id)
                );

        assertAll(
                () -> assertEquals(id, exception.businessId()),
                () -> assertTrue(
                        exception.getMessage().contains(id.toString())
                )
        );

        verify(repository).findById(id);
    }
}