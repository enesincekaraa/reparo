package com.enesincekara.reparo.device.persistence;

import com.enesincekara.reparo.PostgresTestConfiguration;
import com.enesincekara.reparo.business.application.BusinessRegistrationService;
import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import com.enesincekara.reparo.customer.application.CustomerRegistrationService;
import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
class DeviceJpaRepositoryTest {

    @Autowired
    private DeviceJpaRepository deviceRepository;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private BusinessJpaRepository businessRepository;

    @Autowired
    private BusinessRegistrationService businessRegistrationService;

    @Autowired
    private CustomerRegistrationService customerRegistrationService;

    @BeforeEach
    void cleanDatabase() {
        deviceRepository.deleteAll();
        customerRepository.deleteAll();
        businessRepository.deleteAll();
    }

    @Test
    void shouldSaveAndLoadDeviceInsideCustomerScope() {
        Business business =
                businessRegistrationService.register("Deniz Bilgisayar");

        Customer customer = customerRegistrationService.register(
                business.id(),
                "Enes İncekara",
                "+905551112233"
        );

        Device device = Device.register(
                business.id(),
                customer.id(),
                DeviceType.LAPTOP,
                "Apple",
                "MacBook Air",
                "C02-123",
                Instant.parse("2026-09-18T10:00:00Z")
        );

        deviceRepository.saveAndFlush(DeviceEntity.from(device));

        Device loaded = deviceRepository
                .findByIdAndBusinessIdAndCustomerId(
                        device.id(),
                        business.id(),
                        customer.id()
                )
                .orElseThrow()
                .toDomain();

        assertThat(loaded.id()).isEqualTo(device.id());
        assertThat(loaded.businessId()).isEqualTo(business.id());
        assertThat(loaded.customerId()).isEqualTo(customer.id());
        assertThat(loaded.type()).isEqualTo(DeviceType.LAPTOP);
        assertThat(loaded.brand()).isEqualTo("Apple");
    }

    @Test
    void shouldNotFindDeviceOutsideItsTenantOrCustomerScope() {
        Business firstBusiness =
                businessRegistrationService.register("Deniz Bilgisayar");

        Business secondBusiness =
                businessRegistrationService.register("Mavi Teknik");

        Customer firstCustomer = customerRegistrationService.register(
                firstBusiness.id(),
                "Enes İncekara",
                "+905551112233"
        );

        Customer secondCustomer = customerRegistrationService.register(
                firstBusiness.id(),
                "Ayşe Yılmaz",
                "+905551112244"
        );

        Device device = Device.register(
                firstBusiness.id(),
                firstCustomer.id(),
                DeviceType.PHONE,
                "Apple",
                "iPhone",
                null,
                Instant.parse("2026-09-18T10:00:00Z")
        );

        deviceRepository.saveAndFlush(DeviceEntity.from(device));

        Optional<DeviceEntity> wrongBusiness =
                deviceRepository.findByIdAndBusinessIdAndCustomerId(
                        device.id(),
                        secondBusiness.id(),
                        firstCustomer.id()
                );

        Optional<DeviceEntity> wrongCustomer =
                deviceRepository.findByIdAndBusinessIdAndCustomerId(
                        device.id(),
                        firstBusiness.id(),
                        secondCustomer.id()
                );

        assertThat(wrongBusiness).isEmpty();
        assertThat(wrongCustomer).isEmpty();
    }

    @Test
    void shouldRejectCrossTenantCustomerRelationshipAtDatabaseLevel() {
        Business firstBusiness =
                businessRegistrationService.register("Deniz Bilgisayar");

        Business secondBusiness =
                businessRegistrationService.register("Mavi Teknik");

        Customer firstBusinessCustomer =
                customerRegistrationService.register(
                        firstBusiness.id(),
                        "Enes İncekara",
                        "+905551112233"
                );

        Device invalidDevice = Device.register(
                secondBusiness.id(),
                firstBusinessCustomer.id(),
                DeviceType.LAPTOP,
                "Apple",
                "MacBook Air",
                null,
                Instant.parse("2026-09-18T10:00:00Z")
        );

        assertThatThrownBy(() ->
                deviceRepository.saveAndFlush(
                        DeviceEntity.from(invalidDevice)
                )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}