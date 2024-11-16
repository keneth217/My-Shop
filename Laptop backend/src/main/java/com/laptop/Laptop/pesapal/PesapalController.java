package com.laptop.Laptop.pesapal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/pay")
@RequiredArgsConstructor
public class PesapalController {
    private final PesapalService pesapalService;
//    @PostMapping
//    public ResponseEntity<String> checkout(@RequestParam String paymentMethod,
//                                           @RequestParam String merchantReference,
//                                           @RequestParam double amount,
//                                           @RequestParam String description) {
//        String result = pesapalService.processCheckout(paymentMethod, merchantReference, amount, description);
//        return ResponseEntity.ok(result);
//    }
    @GetMapping("/token")
    public ResponseEntity<String> getAccessToken() {
        String token = pesapalService.fetchAccessToken();
        System.out.println(token);
        System.out.println("Access Token: " + getAccessToken());
        return ResponseEntity.ok(token);
    }
    @PostMapping
    public ResponseEntity<Void> handlePesapalCallback(@RequestParam String OrderTrackingId,
                                                      @RequestParam String OrderMerchantReference,
                                                      @RequestParam String OrderNotificationType) {
        // Process callback and update order status in your database
        System.out.println("Callback received: " + OrderTrackingId);
        return ResponseEntity.ok().build();
    }

}
