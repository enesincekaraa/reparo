package com.enesincekara.reparo.customer.application;


import com.enesincekara.reparo.business.application.BusinessNotFoundException;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.domain.PhoneNumber;
import com.enesincekara.reparo.customer.persistence.CustomerEntity;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service

public class CustomerRegistrationService {

    private final BusinessJpaRepository businessRepository;
    private final CustomerJpaRepository customerRepository;
    private final Clock clock;

    public CustomerRegistrationService(
            BusinessJpaRepository businessRepository,
            CustomerJpaRepository customerRepository,
            Clock clock
    ) {
        this.businessRepository = businessRepository;
        this.customerRepository = customerRepository;
        this.clock = clock;
    }




    @Transactional
    public Customer register(
            UUID businessId,
            String fullName,
            String rawPhoneNumber
    ){

        if (!businessRepository.existsById(businessId)) {
            throw new BusinessNotFoundException(businessId);
        }


        PhoneNumber phoneNumber =PhoneNumber.of(rawPhoneNumber);


        Instant createdAt =clock.instant().truncatedTo(ChronoUnit.MICROS);

        Customer customer = Customer.register(
                businessId,
                fullName,
                phoneNumber,
                createdAt
        );

        customerRepository.save(CustomerEntity.from(customer));

        return customer;
    }
}
