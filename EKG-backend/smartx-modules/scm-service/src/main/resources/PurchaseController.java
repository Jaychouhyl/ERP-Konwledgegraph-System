package com.smartx.scm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Purchase Service";
    }
}
