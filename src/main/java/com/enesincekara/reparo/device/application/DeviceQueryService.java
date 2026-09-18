package com.enesincekara.reparo.device.application;

import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.persistence.DeviceJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeviceQueryService {

    private final DeviceJpaRepository deviceRepository;

    public DeviceQueryService(DeviceJpaRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional(readOnly = true)
    public Device getById(
            UUID businessId,
            UUID customerId,
            UUID deviceId
    ) {
        return deviceRepository
                .findByIdAndBusinessIdAndCustomerId(
                        deviceId,
                        businessId,
                        customerId
                )
                .map(entity -> entity.toDomain())
                .orElseThrow(() -> new DeviceNotFoundException(
                        businessId,
                        customerId,
                        deviceId
                ));
    }
}