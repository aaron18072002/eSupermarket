package com.coding.controller;

import com.coding.dto.response.ApiResponse;
import com.coding.dto.webhook.SepayWebhookPayload;
import com.coding.security.SepayHmacValidator;
import com.coding.service.IOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Tag(name = "Payment Webhook", description = "Endpoints for receiving automated payment callbacks from SePay")
@RestController
@RequestMapping("/api/v1/orders/sepay-webhook")
@RequiredArgsConstructor
public class SepayWebhookController {

    private final IOrderService orderService;
    private final SepayHmacValidator hmacValidator;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Receive and verify automated payment webhook from SePay")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleSepayWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Signature", required = false) String xSignature,
            @RequestHeader(value = "Signature", required = false) String signatureHeader) {

        log.info("Received incoming webhook notification from SePay");

        // Determine which header contains the signature or API key
        String signature = signatureHeader;
        if (signature == null || signature.isBlank()) {
            signature = xSignature;
        }
        if (signature == null || signature.isBlank()) {
            signature = authHeader;
        }

        // Validate HMAC-SHA256 signature
        if (!this.hmacValidator.isValidSignature(rawBody, signature)) {
            log.warn("SePay webhook rejected: Invalid signature or unauthorized token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.<Map<String, Object>>builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Invalid webhook signature")
                            .build()
            );
        }

        try {
            SepayWebhookPayload payload = this.objectMapper.readValue(rawBody, SepayWebhookPayload.class);
            boolean processed = this.orderService.processSepayWebhook(payload);

            return ResponseEntity.ok(
                    ApiResponse.<Map<String, Object>>builder()
                            .status(HttpStatus.OK.value())
                            .message(processed ? "Webhook processed successfully" : "Webhook acknowledged but order unmapped")
                            .data(Map.of("success", processed))
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to parse SePay webhook payload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.<Map<String, Object>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Malformed JSON payload: " + e.getMessage())
                            .build()
            );
        }
    }

}
