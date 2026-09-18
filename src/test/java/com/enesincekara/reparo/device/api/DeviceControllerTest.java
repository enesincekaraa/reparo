package com.enesincekara.reparo.device.api;

import com.enesincekara.reparo.PostgresTestConfiguration;
import com.enesincekara.reparo.business.application.BusinessRegistrationService;
import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import com.enesincekara.reparo.customer.application.CustomerRegistrationService;
import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
import com.enesincekara.reparo.device.application.DeviceRegistrationService;
import com.enesincekara.reparo.device.domain.Device;
import com.enesincekara.reparo.device.domain.DeviceType;
import com.enesincekara.reparo.device.persistence.DeviceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestConfiguration.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessRegistrationService businessRegistrationService;

    @Autowired
    private CustomerRegistrationService customerRegistrationService;

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    @Autowired
    private DeviceJpaRepository deviceRepository;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private BusinessJpaRepository businessRepository;

    @BeforeEach
    void cleanDatabase() {
        deviceRepository.deleteAll();
        customerRepository.deleteAll();
        businessRepository.deleteAll();
    }

    @Test
    void shouldCreateAndPersistDevice() throws Exception {
        Business business = registerBusiness("Deniz Bilgisayar");
        Customer customer = registerCustomer(
                business.id(),
                "Enes İncekara",
                "+905551112233"
        );

        mockMvc.perform(post(
                        "/api/v1/businesses/{businessId}"
                                + "/customers/{customerId}/devices",
                        business.id(),
                        customer.id()
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "laptop",
                                  "brand": "  Apple  ",
                                  "model": "  MacBook Air M4  ",
                                  "serialNumber": "  C02-123  "
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        startsWith(
                                "/api/v1/businesses/"
                                        + business.id()
                                        + "/customers/"
                                        + customer.id()
                                        + "/devices/"
                        )
                ))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.businessId")
                        .value(business.id().toString()))
                .andExpect(jsonPath("$.customerId")
                        .value(customer.id().toString()))
                .andExpect(jsonPath("$.type").value("LAPTOP"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.model").value("MacBook Air M4"))
                .andExpect(jsonPath("$.serialNumber").value("C02-123"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertThat(deviceRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldRejectUnsupportedDeviceType() throws Exception {
        Business business = registerBusiness("Deniz Bilgisayar");
        Customer customer = registerCustomer(
                business.id(),
                "Enes İncekara",
                "+905551112233"
        );

        mockMvc.perform(post(
                        "/api/v1/businesses/{businessId}"
                                + "/customers/{customerId}/devices",
                        business.id(),
                        customer.id()
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "TOASTER",
                                  "brand": "Example",
                                  "model": null,
                                  "serialNumber": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.code").value("INVALID_DEVICE"))
                .andExpect(jsonPath("$.field").value("type"));

        assertThat(deviceRepository.count()).isZero();
    }

    @Test
    void shouldRejectBlankDeviceBrand() throws Exception {
        Business business = registerBusiness("Deniz Bilgisayar");
        Customer customer = registerCustomer(
                business.id(),
                "Enes İncekara",
                "+905551112233"
        );

        mockMvc.perform(post(
                        "/api/v1/businesses/{businessId}"
                                + "/customers/{customerId}/devices",
                        business.id(),
                        customer.id()
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "PHONE",
                                  "brand": "   ",
                                  "model": "iPhone",
                                  "serialNumber": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DEVICE"))
                .andExpect(jsonPath("$.field").value("brand"));

        assertThat(deviceRepository.count()).isZero();
    }

    @Test
    void shouldRejectDeviceForMissingCustomer() throws Exception {
        Business business = registerBusiness("Deniz Bilgisayar");
        UUID missingCustomerId = UUID.randomUUID();

        mockMvc.perform(post(
                        "/api/v1/businesses/{businessId}"
                                + "/customers/{customerId}/devices",
                        business.id(),
                        missingCustomerId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "LAPTOP",
                                  "brand": "Apple",
                                  "model": "MacBook Air",
                                  "serialNumber": null
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("CUSTOMER_NOT_FOUND"));

        assertThat(deviceRepository.count()).isZero();
    }

    @Test
    void shouldGetDeviceInsideCorrectScope() throws Exception {
        Business business = registerBusiness("Deniz Bilgisayar");
        Customer customer = registerCustomer(
                business.id(),
                "Enes İncekara",
                "+905551112233"
        );

        Device device = deviceRegistrationService.register(
                business.id(),
                customer.id(),
                DeviceType.LAPTOP,
                "Apple",
                "MacBook Air",
                "C02-123"
        );

        mockMvc.perform(get(
                        "/api/v1/businesses/{businessId}"
                                + "/customers/{customerId}"
                                + "/devices/{deviceId}",
                        business.id(),
                        customer.id(),
                        device.id()
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(device.id().toString()))
                .andExpect(jsonPath("$.businessId")
                        .value(business.id().toString()))
                .andExpect(jsonPath("$.customerId")
                        .value(customer.id().toString()))
                .andExpect(jsonPath("$.type").value("LAPTOP"))
                .andExpect(jsonPath("$.brand").value("Apple"));
    }

    @Test
    void shouldHideDeviceFromAnotherBusiness() throws Exception {
        Business firstBusiness = registerBusiness("Deniz Bilgisayar");
        Business secondBusiness = registerBusiness("Mavi Teknik");

        Customer firstCustomer = registerCustomer(
                firstBusiness.id(),
                "Enes İncekara",
                "+905551112233"
        );

        Customer secondCustomer = registerCustomer(
                secondBusiness.id(),
                "Ayşe Yılmaz",
                "+905551112244"
        );

        Device device = deviceRegistrationService.register(
                firstBusiness.id(),
                firstCustomer.id(),
                DeviceType.PHONE,
                "Apple",
                "iPhone",
                null
        );

        mockMvc.perform(get(
                        "/api/v1/businesses/{businessId}"
                                + "/customers/{customerId}"
                                + "/devices/{deviceId}",
                        secondBusiness.id(),
                        secondCustomer.id(),
                        device.id()
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("DEVICE_NOT_FOUND"));
    }

    @Test
    void shouldHideDeviceFromAnotherCustomerInSameBusiness()
            throws Exception {

        Business business = registerBusiness("Deniz Bilgisayar");

        Customer firstCustomer = registerCustomer(
                business.id(),
                "Enes İncekara",
                "+905551112233"
        );

        Customer secondCustomer = registerCustomer(
                business.id(),
                "Ayşe Yılmaz",
                "+905551112244"
        );

        Device device = deviceRegistrationService.register(
                business.id(),
                firstCustomer.id(),
                DeviceType.LAPTOP,
                "Apple",
                "MacBook Air",
                null
        );

        mockMvc.perform(get(
                        "/api/v1/businesses/{businessId}"
                                + "/customers/{customerId}"
                                + "/devices/{deviceId}",
                        business.id(),
                        secondCustomer.id(),
                        device.id()
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("DEVICE_NOT_FOUND"));
    }

    private Business registerBusiness(String name) {
        return businessRegistrationService.register(name);
    }

    private Customer registerCustomer(
            UUID businessId,
            String fullName,
            String phoneNumber
    ) {
        return customerRegistrationService.register(
                businessId,
                fullName,
                phoneNumber
        );
    }
}