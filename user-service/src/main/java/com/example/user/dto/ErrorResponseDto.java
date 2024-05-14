package com.example.user.dto;

import lombok.Data;

@Data
public class ErrorResponseDto {
    private Integer code;
    private String message;
}
