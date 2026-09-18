package com.enesincekara.reparo.device.application;

import com.enesincekara.reparo.customer.application.CustomerNotFoundException;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;
import com.enesincekara.reparo.device.persistence.DeviceEntity;
import com.enesincekara.reparo.device.persistence.DeviceJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class DeviceRegistrationService {

    private final CustomerJpaRepository customerRepository;
    private final DeviceJpaRepository deviceRepository;
    private final Clock clock;

    public DeviceRegistrationService(CustomerJpaRepository customerRepository, DeviceJpaRepository deviceRepository, Clock clock) {
        this.customerRepository = customerRepository;
        this.deviceRepository = deviceRepository;
        this.clock = clock;
    }

    @Transactional
    public Device register(
            UUID businessId,
            UUID customerId,
            DeviceType type,
            String brand,
            String model,
            String serialNumber
    ){
        boolean customerExistsInBusiness =
                customerRepository.findByIdAndBusinessId(customerId, businessId).isPresent();

        if (!customerExistsInBusiness){
            throw new CustomerNotFoundException(
                    businessId,
                    customerId
            );
        }

        Instant createdAt=clock.instant().truncatedTo(ChronoUnit.MICROS);

        Device device = Device.register(
                businessId,
                customerId,
                type,
                brand,
                model,
                serialNumber,
                createdAt
        );

        deviceRepository.save(DeviceEntity.from(device));
        return device;



    }


}
