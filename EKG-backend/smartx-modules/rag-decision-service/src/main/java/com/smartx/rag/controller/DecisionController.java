package com.smartx.rag.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/decision")
public class DecisionController {

    @PostMapping("/audit")
    public String intelligentAudit() {
        return "Audit Result";
    }

    @PostMapping("/compliance")
    public String checkCompliance() {
        return "Compliance Result";
    }
}
