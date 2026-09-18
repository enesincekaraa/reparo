package com.enesincekara.reparo.customer.application;

import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.domain.PhoneNumber;
import com.enesincekara.reparo.customer.persistence.CustomerEntity;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
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
class CustomerQueryServiceTest {

    @Mock
    private CustomerJpaRepository repository;

    @Test
    void shouldReturnCustomerWithinBusiness() {
        UUID businessId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.restore(
                customerId,
                businessId,
                "Ayşe Yılmaz",
                PhoneNumber.of("+905321234567"),
                Instant.parse("2026-09-17T13:00:00Z")
        );

        when(repository.findByIdAndBusinessId(
                customerId,
                businessId
        )).thenReturn(
                Optional.of(CustomerEntity.from(customer))
        );

        CustomerQueryService service =
                new CustomerQueryService(repository);

        Customer result = service.getById(
                businessId,
                customerId
        );

        assertAll(
                () -> assertEquals(customerId, result.id()),
                () -> assertEquals(
                        businessId,
                        result.businessId()
                ),
                () -> assertEquals(
                        "Ayşe Yılmaz",
                        result.fullName()
                )
        );
    }

    @Test
    void shouldThrowWhenCustomerIsOutsideBusiness() {
        UUID businessId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(repository.findByIdAndBusinessId(
                customerId,
                businessId
        )).thenReturn(Optional.empty());

        CustomerQueryService service =
                new CustomerQueryService(repository);

        CustomerNotFoundException exception =
                assertThrows(
                        CustomerNotFoundException.class,
                        () -> service.getById(
                                businessId,
                                customerId
                        )
                );

        assertAll(
                () -> assertEquals(
                        businessId,
                        exception.businessId()
                ),
                () -> assertEquals(
                        customerId,
                        exception.customerId()
                )
        );
    }
}