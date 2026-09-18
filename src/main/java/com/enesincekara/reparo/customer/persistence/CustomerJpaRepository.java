package com.enesincekara.reparo.customer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByIdAndBusinessId(UUID id, UUID businessId);
}
