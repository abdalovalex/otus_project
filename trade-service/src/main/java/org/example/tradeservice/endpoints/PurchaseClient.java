package org.example.tradeservice.endpoints;

import java.util.List;

import org.example.tradeservice.dto.UserDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "purchase", url = "${app.purchase-service}")
public interface PurchaseClient {
    @GetMapping(value = "/purchase-service/purchase/suppliers/{purchaseId}")
    List<UserDto> getSupplierUsers(@PathVariable Integer purchaseId);
}
