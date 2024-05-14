package org.example.notificationservice.exception;

import org.example.notificationservice.dto.ErrorDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = ResponseStatusException.class)
    protected ResponseEntity<ErrorDto> handleConflict(ResponseStatusException exp) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(0);
        errorDto.setMessage(exp.getReason());
        return new ResponseEntity<>(errorDto, exp.getStatusCode());
    }
}
