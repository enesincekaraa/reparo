package com.enesincekara.reparo.business.application;

import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessEntity;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BusinessQueryService {
    private final BusinessJpaRepository repository;

    public BusinessQueryService(BusinessJpaRepository repository) {
        this.repository = repository;
    }
    @Transactional(readOnly = true)
    public Business get(UUID businessId) {
        return repository.findById(businessId)
                .map(BusinessEntity::toDomain)
                .orElseThrow(
                        ()->new BusinessNotFoundException(businessId)
                );
    }
}
