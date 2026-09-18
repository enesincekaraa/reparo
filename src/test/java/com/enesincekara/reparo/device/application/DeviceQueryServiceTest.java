package com.enesincekara.reparo.device.application;

import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;
import com.enesincekara.reparo.device.persistence.DeviceEntity;
import com.enesincekara.reparo.device.persistence.DeviceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeviceQueryServiceTest {

    private DeviceJpaRepository deviceRepository;
    private DeviceQueryService service;

    @BeforeEach
    void setUp() {
        deviceRepository = mock(DeviceJpaRepository.class);
        service = new DeviceQueryService(deviceRepository);
    }

    @Test
    void shouldReturnDeviceInsideRequestedScope() {
        UUID businessId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        Device device = Device.restore(
                deviceId,
                businessId,
                customerId,
                DeviceType.LAPTOP,
                "Apple",
                "MacBook Air",
                "C02-123",
                Instant.parse("2026-09-18T10:00:00Z")
        );

        when(deviceRepository.findByIdAndBusinessIdAndCustomerId(
                deviceId,
                businessId,
                customerId
        )).thenReturn(Optional.of(DeviceEntity.from(device)));

        Device result = service.getById(
                businessId,
                customerId,
                deviceId
        );

        assertThat(result.id()).isEqualTo(deviceId);
        assertThat(result.businessId()).isEqualTo(businessId);
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.brand()).isEqualTo("Apple");
    }

    @Test
    void shouldThrowWhenDeviceIsOutsideRequestedScope() {
        UUID businessId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        when(deviceRepository.findByIdAndBusinessIdAndCustomerId(
                deviceId,
                businessId,
                customerId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(
                businessId,
                customerId,
                deviceId
        ))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining(deviceId.toString());
    }
}