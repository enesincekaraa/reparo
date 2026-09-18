package com.enesincekara.reparo.customer.persistence;

import com.enesincekara.reparo.PostgresTestConfiguration;
import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessEntity;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import com.enesincekara.reparo.customer.domain.Customer;
import com.enesincekara.reparo.customer.domain.PhoneNumber;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Import(PostgresTestConfiguration.class)
@Transactional
class CustomerJpaRepositoryTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-09-17T13:00:00Z");

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Autowired
    private BusinessJpaRepository businessRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSaveAndLoadCustomerWithinBusiness() {

        Business business = saveBusiness("Deniz Bilgisayar");

        Business other = saveBusiness("Kent Teknik");

        Customer customer = Customer.register(
                business.id(),
                "Ayşe Yılmaz",
                PhoneNumber.of("+905321234567"),
                CREATED_AT
        );
        customerRepository.saveAndFlush(
                CustomerEntity.from(customer)
        );


        entityManager.clear();



        CustomerEntity loaded=customerRepository.findByIdAndBusinessId(
                customer.id(),
                business.id()
        ).orElseThrow();


        assertAll(
                () -> assertEquals(
                        customer.id(),
                        loaded.getId()
                ),
                () -> assertEquals(
                        business.id(),
                        loaded.getBusinessId()
                ),
                () -> assertEquals(
                        "Ayşe Yılmaz",
                        loaded.getFullName()
                ),
                () -> assertEquals(
                        "+905321234567",
                        loaded.getPhoneNumber()
                )
        );

    }

    @Test
    void shouldNotFindCustomerUnderAnotherBusiness() {
        Business owner = saveBusiness("Deniz Bilgisayar");
        Business other = saveBusiness("Kent Teknik");

        Customer customer = Customer.register(
                owner.id(),
                "Ayşe Yılmaz",
                PhoneNumber.of("+905321234567"),
                CREATED_AT
        );

        customerRepository.saveAndFlush(
                CustomerEntity.from(customer)
        );

        entityManager.clear();

        assertTrue(
                customerRepository.findByIdAndBusinessId(
                        customer.id(),
                        other.id()
                ).isEmpty()
        );
    }

    private Business saveBusiness(String name) {
        Business business = Business.register(name, CREATED_AT);
        businessRepository.saveAndFlush(BusinessEntity.from(business));
        return business;
    }

}