package org.example.purchaseservice.exception;

import org.example.purchaseservice.dto.ErrorDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = ResponseStatusException.class)
    protected ResponseEntity<ErrorDto> handleConflict(ResponseStatusException exp) {
        ErrorDto ErrorDto = new ErrorDto();
        ErrorDto.setCode(0);
        ErrorDto.setMessage(exp.getReason());
        return new ResponseEntity<>(ErrorDto, exp.getStatusCode());
    }
}
