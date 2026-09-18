package com.enesincekara.reparo.customer.api;

import com.enesincekara.reparo.business.application.BusinessNotFoundException;
import com.enesincekara.reparo.customer.application.CustomerNotFoundException;
import com.enesincekara.reparo.customer.domain.InvalidCustomerNameException;
import com.enesincekara.reparo.customer.domain.InvalidPhoneNumberException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(assignableTypes = CustomerController.class)
public class CustomerExceptionHandler {

    @ExceptionHandler(InvalidCustomerNameException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCustomerName(
            InvalidCustomerNameException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid customer name",
                exception.getMessage(),
                "INVALID_CUSTOMER_NAME",
                request
        );
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPhoneNumber(
            InvalidPhoneNumberException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid phone number",
                exception.getMessage(),
                "INVALID_PHONE_NUMBER",
                request
        );
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBusinessNotFound(
            BusinessNotFoundException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Business not found",
                exception.getMessage(),
                "BUSINESS_NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCustomerNotFound(
            CustomerNotFoundException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Customer not found",
                exception.getMessage(),
                "CUSTOMER_NOT_FOUND",
                request
        );
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status,
            String title,
            String detail,
            String code,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);

        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}