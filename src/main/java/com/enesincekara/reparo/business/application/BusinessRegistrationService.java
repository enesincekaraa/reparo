package com.enesincekara.reparo.business.application;

import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessEntity;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class BusinessRegistrationService {
    private final BusinessJpaRepository repository;
    private final Clock clock;

    public BusinessRegistrationService(BusinessJpaRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }



    @Transactional
    public Business register(String name){
        Instant createdAt=clock.instant().truncatedTo(ChronoUnit.MICROS);

        Business business = Business.register(name, createdAt);

        BusinessEntity entity = BusinessEntity.from(business);
        repository.save(entity);

        return business;
    }
}
