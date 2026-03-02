package com.smartx.auth.controller;

import com.smartx.auth.domain.entity.Customer;
import com.smartx.auth.domain.entity.Supplier;
import com.smartx.auth.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/partner")
public class PartnerController {

    @Autowired 
    private PartnerService partnerService;

    @GetMapping("/customers")
    public Map<String, Object> listCustomers() {
        List<Customer> list = partnerService.getAllCustomers();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        result.put("msg", "success");
        return result;
    }

    @GetMapping("/suppliers")
    public Map<String, Object> listSuppliers() {
        List<Supplier> list = partnerService.getAllSuppliers();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        result.put("msg", "success");
        return result;
    }
}
