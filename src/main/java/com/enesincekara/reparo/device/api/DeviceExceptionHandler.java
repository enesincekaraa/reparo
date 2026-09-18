package com.enesincekara.reparo.device.api;

import com.enesincekara.reparo.customer.application.CustomerNotFoundException;
import com.enesincekara.reparo.device.application.DeviceNotFoundException;
import com.enesincekara.reparo.device.domain.InvalidDeviceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(assignableTypes = DeviceController.class)
public class DeviceExceptionHandler {

    @ExceptionHandler(InvalidDeviceException.class)
    public ResponseEntity<ProblemDetail> handleInvalidDevice(
            InvalidDeviceException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid device",
                exception.getMessage(),
                "INVALID_DEVICE",
                request
        );

        problem.setProperty("field", exception.field());

        return response(HttpStatus.BAD_REQUEST, problem);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCustomerNotFound(
            CustomerNotFoundException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = createProblem(
                HttpStatus.NOT_FOUND,
                "Customer not found",
                exception.getMessage(),
                "CUSTOMER_NOT_FOUND",
                request
        );

        return response(HttpStatus.NOT_FOUND, problem);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleDeviceNotFound(
            DeviceNotFoundException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = createProblem(
                HttpStatus.NOT_FOUND,
                "Device not found",
                exception.getMessage(),
                "DEVICE_NOT_FOUND",
                request
        );

        return response(HttpStatus.NOT_FOUND, problem);
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            String code,
            HttpServletRequest request
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, detail);

        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code);

        return problem;
    }

    private ResponseEntity<ProblemDetail> response(
            HttpStatus status,
            ProblemDetail problem
    ) {
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}