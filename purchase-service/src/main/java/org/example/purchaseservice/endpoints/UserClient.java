package org.example.purchaseservice.endpoints;

import java.util.List;

import org.example.purchaseservice.dto.UserDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "user", url = "${app.user-service}")
public interface UserClient {
    @GetMapping(value = "/user-service/user/suppliers")
    List<UserDto> getSupplierUsers();
}
