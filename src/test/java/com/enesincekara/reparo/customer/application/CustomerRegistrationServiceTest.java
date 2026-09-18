package com.enesincekara.reparo.customer.application;

import com.enesincekara.reparo.business.application.BusinessNotFoundException;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.domain.InvalidPhoneNumberException;
import com.enesincekara.reparo.customer.persistence.CustomerEntity;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    private static final UUID BUSINESS_ID =
            UUID.fromString(
                    "ba530d61-63cb-4c8c-8c18-8bc39fb61f2f"
            );

    @Mock
    private BusinessJpaRepository businessRepository;

    @Mock
    private CustomerJpaRepository customerRepository;

    private CustomerRegistrationService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse(
                        "2026-09-17T13:00:00.123456789Z"
                ),
                ZoneOffset.UTC
        );

        service = new CustomerRegistrationService(
                businessRepository,
                customerRepository,
                clock
        );
    }

    @Test
    void shouldRegisterCustomerForExistingBusiness() {
        when(businessRepository.existsById(BUSINESS_ID))
                .thenReturn(true);

        Customer customer = service.register(
                BUSINESS_ID,
                "  Ayşe Yılmaz  ",
                "+905321234567"
        );

        ArgumentCaptor<CustomerEntity> captor =
                ArgumentCaptor.forClass(CustomerEntity.class);

        verify(customerRepository).save(captor.capture());

        CustomerEntity saved = captor.getValue();

        assertAll(
                () -> assertEquals(
                        BUSINESS_ID,
                        customer.businessId()
                ),
                () -> assertEquals(
                        "Ayşe Yılmaz",
                        customer.fullName()
                ),
                () -> assertEquals(
                        "+905321234567",
                        customer.phoneNumber().value()
                ),
                () -> assertEquals(
                        Instant.parse(
                                "2026-09-17T13:00:00.123456Z"
                        ),
                        customer.createdAt()
                ),
                () -> assertEquals(
                        customer.id(),
                        saved.getId()
                )
        );
    }

    @Test
    void shouldRejectCustomerForMissingBusiness() {
        when(businessRepository.existsById(BUSINESS_ID))
                .thenReturn(false);

        assertThrows(
                BusinessNotFoundException.class,
                () -> service.register(
                        BUSINESS_ID,
                        "Ayşe Yılmaz",
                        "+905321234567"
                )
        );

        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldNotSaveCustomerWithInvalidPhoneNumber() {
        when(businessRepository.existsById(BUSINESS_ID))
                .thenReturn(true);

        assertThrows(
                InvalidPhoneNumberException.class,
                () -> service.register(
                        BUSINESS_ID,
                        "Ayşe Yılmaz",
                        "0532 123 45 67"
                )
        );

        verifyNoInteractions(customerRepository);
    }
}