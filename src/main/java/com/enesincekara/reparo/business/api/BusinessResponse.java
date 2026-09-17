package com.enesincekara.reparo.business.api;

import com.enesincekara.reparo.business.domain.Business;

import java.time.Instant;
import java.util.UUID;

public record BusinessResponse(
        UUID id,
        String name,
        Instant createdAt
) {
    public static BusinessResponse from(Business business) {
        return new BusinessResponse(
                business.id(),
                business.name(),
                business.createdAt()
        );
    }
}
