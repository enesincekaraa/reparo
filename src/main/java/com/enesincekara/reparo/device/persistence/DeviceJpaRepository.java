package com.enesincekara.reparo.device.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceJpaRepository extends JpaRepository<DeviceEntity, UUID> {
    Optional<DeviceEntity> findByIdAndBusinessIdAndCustomerId(
            UUID id,
            UUID businessId,
            UUID customerId
    );
}
