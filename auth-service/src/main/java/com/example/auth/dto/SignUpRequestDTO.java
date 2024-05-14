package com.example.auth.dto;

import com.example.auth.entity.Role;
import lombok.Data;

@Data
public class SignUpRequestDTO {
    private Integer id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
