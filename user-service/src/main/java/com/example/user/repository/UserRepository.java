package com.example.user.repository;

import java.util.List;
import java.util.Optional;

import com.example.user.entity.Role;
import com.example.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);
}
