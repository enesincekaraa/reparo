package com.enesincekara.reparo.customer.api;

import com.enesincekara.reparo.PostgresTestConfiguration;
import com.enesincekara.reparo.business.application.BusinessRegistrationService;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import com.enesincekara.reparo.customer.application.CustomerRegistrationService;
import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
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
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessRegistrationService businessRegistrationService;

    @Autowired
    private CustomerRegistrationService customerRegistrationService;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private BusinessJpaRepository businessRepository;

    @BeforeEach
    void cleanDatabase() {
        customerRepository.deleteAll();
        businessRepository.deleteAll();
    }

    @Test
    void shouldCreateAndPersistCustomer() throws Exception {
        UUID businessId = registerBusiness("Deniz Bilgisayar");

        mockMvc.perform(post("/api/v1/businesses/{businessId}/customers", businessId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "  Enes İncekara  ",
                                  "phoneNumber": "+905551112233"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        startsWith("/api/v1/businesses/" + businessId + "/customers/")
                ))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.businessId").value(businessId.toString()))
                .andExpect(jsonPath("$.fullName").value("Enes İncekara"))
                .andExpect(jsonPath("$.phoneNumber").value("+905551112233"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertThat(customerRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldRejectBlankCustomerName() throws Exception {
        UUID businessId = registerBusiness("Deniz Bilgisayar");

        mockMvc.perform(post("/api/v1/businesses/{businessId}/customers", businessId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "   ",
                                  "phoneNumber": "+905551112233"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.code")
                        .value("INVALID_CUSTOMER_NAME"));

        assertThat(customerRepository.count()).isZero();
    }

    @Test
    void shouldRejectInvalidPhoneNumber() throws Exception {
        UUID businessId = registerBusiness("Deniz Bilgisayar");

        mockMvc.perform(post("/api/v1/businesses/{businessId}/customers", businessId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Enes İncekara",
                                  "phoneNumber": "0555 111 22 33"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("INVALID_PHONE_NUMBER"));

        assertThat(customerRepository.count()).isZero();
    }

    @Test
    void shouldReturnNotFoundWhenBusinessDoesNotExist() throws Exception {
        UUID missingBusinessId = UUID.randomUUID();

        mockMvc.perform(post(
                        "/api/v1/businesses/{businessId}/customers",
                        missingBusinessId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Enes İncekara",
                                  "phoneNumber": "+905551112233"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("BUSINESS_NOT_FOUND"));

        assertThat(customerRepository.count()).isZero();
    }

    @Test
    void shouldGetCustomerInsideSameBusiness() throws Exception {
        UUID businessId = registerBusiness("Deniz Bilgisayar");

        Customer customer = customerRegistrationService.register(
                businessId,
                "Enes İncekara",
                "+905551112233"
        );

        mockMvc.perform(get(
                        "/api/v1/businesses/{businessId}/customers/{customerId}",
                        businessId,
                        customer.id()
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(customer.id().toString()))
                .andExpect(jsonPath("$.businessId")
                        .value(businessId.toString()))
                .andExpect(jsonPath("$.fullName")
                        .value("Enes İncekara"))
                .andExpect(jsonPath("$.phoneNumber")
                        .value("+905551112233"));
    }

    @Test
    void shouldHideCustomerFromAnotherBusiness() throws Exception {
        UUID firstBusinessId = registerBusiness("Deniz Bilgisayar");
        UUID secondBusinessId = registerBusiness("Mavi Teknik");

        Customer customer = customerRegistrationService.register(
                firstBusinessId,
                "Enes İncekara",
                "+905551112233"
        );

        mockMvc.perform(get(
                        "/api/v1/businesses/{businessId}/customers/{customerId}",
                        secondBusinessId,
                        customer.id()
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("CUSTOMER_NOT_FOUND"));
    }

    private UUID registerBusiness(String name) {
        return businessRegistrationService.register(name).id();
    }
}