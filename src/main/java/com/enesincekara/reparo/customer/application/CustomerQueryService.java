package com.enesincekara.reparo.customer.application;


import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.persistence.CustomerEntity;
import com.enesincekara.reparo.customer.persistence.CustomerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerQueryService {

    private final CustomerJpaRepository repository;

    public CustomerQueryService(CustomerJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Customer getById(
            UUID businessId,
            UUID customerId
    ){
        return repository.findByIdAndBusinessId(customerId,businessId)
                .map(CustomerEntity::toDomain)
                .orElseThrow(
                        ()-> new CustomerNotFoundException(
                                businessId,
                                customerId
                        )
                );
    }
}
