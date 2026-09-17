package com.enesincekara.reparo.business.api;

import com.enesincekara.reparo.business.application.BusinessQueryService;
import com.enesincekara.reparo.business.application.BusinessRegistrationService;
import com.enesincekara.reparo.business.domain.Business;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {

    private final BusinessRegistrationService registrationService;
    private final BusinessQueryService queryService;

    public BusinessController(
            BusinessRegistrationService registrationService,
            BusinessQueryService queryService
    ) {
        this.registrationService = registrationService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<BusinessResponse> register(
            @RequestBody RegisterBusinessRequest request
    ) {
        Business business = registrationService.register(
                request.name()
        );

        BusinessResponse response =
                BusinessResponse.from(business);

        URI location = URI.create(
                "/api/v1/businesses/" + business.id()
        );

        return ResponseEntity.created(location)
                .body(response);
    }

    @GetMapping("/{businessId}")
    public BusinessResponse get(
            @PathVariable UUID businessId
    ) {
        Business business = queryService.get(businessId);

        return BusinessResponse.from(business);
    }
}