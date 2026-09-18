package com.enesincekara.reparo.device.persistence;

import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class DeviceEntity {

    @Id
    private UUID id;

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 32)
    private DeviceType type;

    @Column(name = "brand", nullable = false, length = 80)
    private String brand;

    @Column(name = "model", length = 120)
    private String model;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected DeviceEntity() {
        // JPA tarafından kullanılır.
    }

    private DeviceEntity(
            UUID id,
            UUID businessId,
            UUID customerId,
            DeviceType type,
            String brand,
            String model,
            String serialNumber,
            Instant createdAt
    ) {
        this.id = id;
        this.businessId = businessId;
        this.customerId = customerId;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.createdAt = createdAt;
    }

    public static DeviceEntity from(Device device) {
        return new DeviceEntity(
                device.id(),
                device.businessId(),
                device.customerId(),
                device.type(),
                device.brand(),
                device.model(),
                device.serialNumber(),
                device.createdAt()
        );
    }

    public Device toDomain() {
        return Device.restore(
                id,
                businessId,
                customerId,
                type,
                brand,
                model,
                serialNumber,
                createdAt
        );
    }
}