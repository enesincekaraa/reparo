package com.enesincekara.reparo.device.application;

import com.enesincekara.reparo.customer.application.CustomerNotFoundException;
import com.enesincekara.reparo.customer.persistence.CustomerEntity;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;
import com.enesincekara.reparo.device.domain.InvalidDeviceException;
import com.enesincekara.reparo.device.persistence.DeviceEntity;
import com.enesincekara.reparo.device.persistence.DeviceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeviceRegistrationServiceTest {

    private static final Instant NOW =
            Instant.parse("2026-09-18T10:00:00.123456Z");

    private CustomerJpaRepository customerRepository;
    private DeviceJpaRepository deviceRepository;
    private DeviceRegistrationService service;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerJpaRepository.class);
        deviceRepository = mock(DeviceJpaRepository.class);

        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

        service = new DeviceRegistrationService(
                customerRepository,
                deviceRepository,
                clock
        );
    }

    @Test
    void shouldRegisterDeviceForCustomerInsideBusiness() {
        UUID businessId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findByIdAndBusinessId(
                customerId,
                businessId
        )).thenReturn(Optional.of(mock(CustomerEntity.class)));

        Device result = service.register(
                businessId,
                customerId,
                DeviceType.LAPTOP,
                "  Apple  ",
                "  MacBook Air  ",
                "  C02-123  "
        );

        ArgumentCaptor<DeviceEntity> captor =
                ArgumentCaptor.forClass(DeviceEntity.class);

        verify(deviceRepository).save(captor.capture());

        Device persisted = captor.getValue().toDomain();

        assertThat(result.id()).isNotNull();
        assertThat(result.businessId()).isEqualTo(businessId);
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.brand()).isEqualTo("Apple");
        assertThat(result.createdAt()).isEqualTo(NOW);

        assertThat(persisted.id()).isEqualTo(result.id());
        assertThat(persisted.brand()).isEqualTo("Apple");
    }

    @Test
    void shouldRejectCustomerOutsideBusiness() {
        UUID businessId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findByIdAndBusinessId(
                customerId,
                businessId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(
                businessId,
                customerId,
                DeviceType.PHONE,
                "Apple",
                "iPhone",
                null
        )).isInstanceOf(CustomerNotFoundException.class);

        verifyNoInteractions(deviceRepository);
    }

    @Test
    void shouldNotSaveInvalidDevice() {
        UUID businessId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findByIdAndBusinessId(
                customerId,
                businessId
        )).thenReturn(Optional.of(mock(CustomerEntity.class)));

        assertThatThrownBy(() -> service.register(
                businessId,
                customerId,
                DeviceType.LAPTOP,
                "   ",
                "MacBook Air",
                null
        )).isInstanceOf(InvalidDeviceException.class);

        verify(deviceRepository, never()).save(any());
    }
}