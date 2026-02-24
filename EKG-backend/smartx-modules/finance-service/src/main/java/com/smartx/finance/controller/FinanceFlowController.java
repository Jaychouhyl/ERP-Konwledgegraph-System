package com.smartx.finance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
public class FinanceFlowController {

    @GetMapping("/flow")
    public String getFlow() {
        return "Finance Flow Data";
    }
}
