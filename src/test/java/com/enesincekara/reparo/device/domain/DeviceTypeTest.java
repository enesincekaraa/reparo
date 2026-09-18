package com.enesincekara.reparo.device.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceTypeTest {

    @Test
    void shouldParseDeviceTypeIgnoringCaseAndWhitespace() {
        assertThat(DeviceType.from(" laptop "))
                .isEqualTo(DeviceType.LAPTOP);

        assertThat(DeviceType.from("Game_Console"))
                .isEqualTo(DeviceType.GAME_CONSOLE);
    }

    @Test
    void shouldRejectMissingOrUnsupportedDeviceType() {
        assertThatThrownBy(() -> DeviceType.from(null))
                .isInstanceOf(InvalidDeviceException.class)
                .hasMessage("Device type must not be blank");

        assertThatThrownBy(() -> DeviceType.from("TOASTER"))
                .isInstanceOf(InvalidDeviceException.class)
                .hasMessage("Unsupported device type: TOASTER");
    }
}