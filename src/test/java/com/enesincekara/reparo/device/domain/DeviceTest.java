package com.enesincekara.reparo.device.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceTest {

    private static final UUID BUSINESS_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final Instant CREATED_AT =
            Instant.parse("2026-09-18T10:00:00Z");

    @Test
    void shouldRegisterAndNormalizeDevice() {
        Device device = Device.register(
                BUSINESS_ID,
                CUSTOMER_ID,
                DeviceType.LAPTOP,
                "  Apple  ",
                "  MacBook Air M4  ",
                "  C02ABC123  ",
                CREATED_AT
        );

        assertThat(device.id()).isNotNull();
        assertThat(device.businessId()).isEqualTo(BUSINESS_ID);
        assertThat(device.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(device.type()).isEqualTo(DeviceType.LAPTOP);
        assertThat(device.brand()).isEqualTo("Apple");
        assertThat(device.model()).isEqualTo("MacBook Air M4");
        assertThat(device.serialNumber()).isEqualTo("C02ABC123");
        assertThat(device.createdAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void shouldConvertBlankOptionalValuesToNull() {
        Device device = Device.register(
                BUSINESS_ID,
                CUSTOMER_ID,
                DeviceType.DESKTOP,
                "Toplama Bilgisayar",
                "   ",
                null,
                CREATED_AT
        );

        assertThat(device.model()).isNull();
        assertThat(device.serialNumber()).isNull();
    }

    @Test
    void shouldRejectMissingDeviceType() {
        assertThatThrownBy(() -> Device.register(
                BUSINESS_ID,
                CUSTOMER_ID,
                null,
                "Apple",
                "MacBook Air",
                null,
                CREATED_AT
        ))
                .isInstanceOf(InvalidDeviceException.class)
                .hasMessage("Device type must not be null");
    }

    @Test
    void shouldRejectBlankBrand() {
        assertThatThrownBy(() -> Device.register(
                BUSINESS_ID,
                CUSTOMER_ID,
                DeviceType.PHONE,
                "   ",
                "iPhone 17",
                null,
                CREATED_AT
        ))
                .isInstanceOf(InvalidDeviceException.class)
                .hasMessage("Device brand must not be blank");
    }

    @Test
    void shouldRejectFieldsExceedingMaximumLength() {
        assertThatThrownBy(() -> Device.register(
                BUSINESS_ID,
                CUSTOMER_ID,
                DeviceType.OTHER,
                "💻".repeat(81),
                null,
                null,
                CREATED_AT
        ))
                .isInstanceOf(InvalidDeviceException.class)
                .hasMessageContaining("80 code points");

        assertThatThrownBy(() -> Device.register(
                BUSINESS_ID,
                CUSTOMER_ID,
                DeviceType.OTHER,
                "Custom",
                "M".repeat(121),
                null,
                CREATED_AT
        ))
                .isInstanceOf(InvalidDeviceException.class)
                .hasMessageContaining("120 code points");

        assertThatThrownBy(() -> Device.register(
                BUSINESS_ID,
                CUSTOMER_ID,
                DeviceType.OTHER,
                "Custom",
                null,
                "S".repeat(101),
                CREATED_AT
        ))
                .isInstanceOf(InvalidDeviceException.class)
                .hasMessageContaining("100 code points");
    }

    @Test
    void shouldRestoreDeviceFromPersistence() {
        UUID deviceId = UUID.randomUUID();

        Device device = Device.restore(
                deviceId,
                BUSINESS_ID,
                CUSTOMER_ID,
                DeviceType.TABLET,
                "Samsung",
                "Galaxy Tab",
                "TAB-123",
                CREATED_AT
        );

        assertThat(device.id()).isEqualTo(deviceId);
        assertThat(device.businessId()).isEqualTo(BUSINESS_ID);
        assertThat(device.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(device.type()).isEqualTo(DeviceType.TABLET);
        assertThat(device.brand()).isEqualTo("Samsung");
    }
}