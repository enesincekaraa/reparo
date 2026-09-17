package com.enesincekara.reparo.business.persistence;

import com.enesincekara.reparo.PostgresTestConfiguration;
import com.enesincekara.reparo.business.domain.Business;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
@Transactional
class BusinessJpaRepositoryTest {

    @Autowired
    private BusinessJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSaveAndLoadBusiness() {
        Instant createdAt =
                Instant.parse("2026-09-16T12:00:00Z");

        Business business = Business.register(
                "Deniz Bilgisayar",
                createdAt
        );

        BusinessEntity entity = BusinessEntity.from(business);

        repository.saveAndFlush(entity);

        entityManager.clear();

        BusinessEntity loaded = repository.findById(business.id())
                .orElseThrow();

        assertAll(
                () -> assertEquals(
                        business.id(),
                        loaded.getId()
                ),
                () -> assertEquals(
                        business.name(),
                        loaded.getName()
                ),
                () -> assertEquals(
                        business.createdAt(),
                        loaded.getCreatedAt()
                )
        );
    }
}