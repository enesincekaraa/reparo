package com.enesincekara.reparo.customer.api;


import com.enesincekara.reparo.customer.application.CustomerQueryService;
import com.enesincekara.reparo.customer.application.CustomerRegistrationService;
import com.enesincekara.reparo.customer.domain.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/customers")
public class CustomerController {

    private final CustomerRegistrationService registrationService;
    private final CustomerQueryService queryService;


    public CustomerController(CustomerRegistrationService registrationService, CustomerQueryService queryService) {
        this.registrationService = registrationService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> register(
            @PathVariable UUID businessId,
            @RequestBody RegisterCustomerRequest request
            ){
        Customer customer = registrationService.register(
                businessId,
                request.fullName(),
                request.phoneNumber()
        );

        URI location=URI.create("/api/v1/businesses/%s/customers/%s".formatted(businessId,customer.id()));


        return ResponseEntity.created(location).body(CustomerResponse.from(customer));

    }

    @GetMapping("/{customerId}")
    public CustomerResponse getById(
            @PathVariable UUID businessId,
            @PathVariable UUID customerId
    ) {
        Customer customer = queryService.getById(businessId, customerId);

        return CustomerResponse.from(customer);
    }
}
