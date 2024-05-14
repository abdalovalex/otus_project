package org.example.purchaseservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserDto<T> {
    private T data;
}
