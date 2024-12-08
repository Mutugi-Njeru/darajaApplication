package com.jacpower.darajaApplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/stkpush")
    public ResponseEntity<String> initiatePayment(@RequestParam String phoneNumber, @RequestParam double amount) {
        String response = paymentService.initiateStkPush(phoneNumber, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> callbackData) {
        // Handle callback logic here
        System.out.println(callbackData);
        return ResponseEntity.ok("Callback received");
    }
}
