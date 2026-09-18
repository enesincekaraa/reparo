package com.enesincekara.reparo.device.api;

import com.enesincekara.reparo.device.application.DeviceQueryService;
import com.enesincekara.reparo.device.application.DeviceRegistrationService;
import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/businesses/{businessId}"
                + "/customers/{customerId}"
                + "/devices"
)
public class DeviceController {

    private final DeviceRegistrationService registrationService;
    private final DeviceQueryService queryService;

    public DeviceController(
            DeviceRegistrationService registrationService,
            DeviceQueryService queryService
    ) {
        this.registrationService = registrationService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> register(
            @PathVariable UUID businessId,
            @PathVariable UUID customerId,
            @RequestBody RegisterDeviceRequest request
    ) {
        DeviceType type = DeviceType.from(request.type());

        Device device = registrationService.register(
                businessId,
                customerId,
                type,
                request.brand(),
                request.model(),
                request.serialNumber()
        );

        URI location = URI.create(
                "/api/v1/businesses/%s/customers/%s/devices/%s"
                        .formatted(
                                businessId,
                                customerId,
                                device.id()
                        )
        );

        return ResponseEntity
                .created(location)
                .body(DeviceResponse.from(device));
    }

    @GetMapping("/{deviceId}")
    public DeviceResponse getById(
            @PathVariable UUID businessId,
            @PathVariable UUID customerId,
            @PathVariable UUID deviceId
    ) {
        Device device = queryService.getById(
                businessId,
                customerId,
                deviceId
        );

        return DeviceResponse.from(device);
    }
}