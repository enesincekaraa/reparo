package com.enesincekara.reparo.business.persistence;

import com.enesincekara.reparo.business.domain.Business;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name ="businesses" )
public class BusinessEntity {

    @Id
    @Column(name = "id",nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    protected BusinessEntity() {}

    public Business toDomain() {
        return Business.restore(
                id,
                name,
                createdAt
        );
    }

    private BusinessEntity(UUID id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static BusinessEntity from(Business business) {
        return new BusinessEntity(
                business.id(),
                business.name(),
                business.createdAt()
        );
    }
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
