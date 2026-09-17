package com.enesincekara.reparo.business.api;

import com.enesincekara.reparo.business.application.BusinessNotFoundException;
import com.enesincekara.reparo.business.domain.InvalidBusinessNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(assignableTypes = BusinessController.class)
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidBusinessNameException.class)
    public ResponseEntity<ProblemDetail> handleInvalidBusinessName(InvalidBusinessNameException exception){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );

        problem.setTitle("Invalid business name");
        problem.setProperty("code", "INVALID_BUSINESS_NAME");

        return ResponseEntity.badRequest()
                .body(problem);
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBusinessNotFound(
            BusinessNotFoundException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problem.setTitle("Business not found");
        problem.setProperty("code", "BUSINESS_NOT_FOUND");
        problem.setProperty(
                "businessId",
                exception.businessId()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problem);
    }
}
