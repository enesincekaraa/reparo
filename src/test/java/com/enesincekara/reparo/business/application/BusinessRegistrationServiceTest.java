package com.enesincekara.reparo.business.application;

import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessEntity;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessRegistrationServiceTest {

    @Mock
    private BusinessJpaRepository repository;

    private BusinessRegistrationService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-16T12:00:00.123456789Z"),
                ZoneOffset.UTC
        );

        service = new BusinessRegistrationService(
                repository,
                clock
        );
    }

    @Test
    void shouldRegisterAndSaveBusiness() {
        Business result = service.register(
                "  Deniz Bilgisayar  "
        );

        ArgumentCaptor<BusinessEntity> captor =
                ArgumentCaptor.forClass(BusinessEntity.class);

        verify(repository).save(captor.capture());

        BusinessEntity saved = captor.getValue();

        Instant expectedCreatedAt =
                Instant.parse("2026-09-16T12:00:00.123456Z");

        assertAll(
                () -> assertEquals(
                        "Deniz Bilgisayar",
                        result.name()
                ),
                () -> assertEquals(
                        expectedCreatedAt,
                        result.createdAt()
                ),
                () -> assertEquals(
                        result.id(),
                        saved.getId()
                ),
                () -> assertEquals(
                        result.name(),
                        saved.getName()
                ),
                () -> assertEquals(
                        result.createdAt(),
                        saved.getCreatedAt()
                )
        );
    }

    @Test
    void shouldNotSaveBusinessWhenNameIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.register("   ")
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldPropagatePersistenceFailure() {
        DataAccessResourceFailureException failure =
                new DataAccessResourceFailureException(
                        "Database unavailable"
                );

        when(repository.save(any(BusinessEntity.class)))
                .thenThrow(failure);

        DataAccessResourceFailureException thrown =
                assertThrows(
                        DataAccessResourceFailureException.class,
                        () -> service.register("Deniz Bilgisayar")
                );

        assertSame(failure, thrown);
    }
}