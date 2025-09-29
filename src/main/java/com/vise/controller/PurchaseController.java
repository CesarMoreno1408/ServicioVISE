package com.vise.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vise.service.PurchaseService;
import com.vise.service.PurchaseService.PurchaseRequest;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @PostMapping
    public Object process(@RequestBody PurchaseRequest request) {
        return service.processPurchase(request);
    }
}
