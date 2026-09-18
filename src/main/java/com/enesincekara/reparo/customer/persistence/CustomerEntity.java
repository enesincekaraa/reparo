package com.enesincekara.reparo.customer.persistence;

import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.domain.PhoneNumber;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(
            name = "business_id",
            nullable = false,
            updatable = false
    )
    private UUID businessId;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(
            name = "phone_number",
            nullable = false,
            length = 16
    )
    private String phoneNumber;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    protected CustomerEntity() {
    }

    private CustomerEntity(
            UUID id,
            UUID businessId,
            String fullName,
            String phoneNumber,
            Instant createdAt
    ) {
        this.id = id;
        this.businessId = businessId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    public static CustomerEntity from(Customer customer) {
        return new CustomerEntity(
                customer.id(),
                customer.businessId(),
                customer.fullName(),
                customer.phoneNumber().value(),
                customer.createdAt()
        );
    }

    public Customer toDomain() {
        return Customer.restore(
                id,
                businessId,
                fullName,
                PhoneNumber.of(phoneNumber),
                createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getBusinessId() {
        return businessId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}