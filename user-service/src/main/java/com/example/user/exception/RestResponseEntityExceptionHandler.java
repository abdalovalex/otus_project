package com.example.user.exception;

import com.example.user.dto.ErrorResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = ResponseStatusException.class)
    protected ResponseEntity<ErrorResponseDto> handleConflict(ResponseStatusException exp) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto();
        errorResponseDTO.setCode(0);
        errorResponseDTO.setMessage(exp.getReason());
        return new ResponseEntity<>(errorResponseDTO, exp.getStatusCode());
    }
}
