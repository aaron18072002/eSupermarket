package com.coding.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Slf4j
@Component
public class SepayHmacValidator {

    private final String secretKey;

    public SepayHmacValidator(@Value("${sepay.secret-key:your_sepay_secret_key}") String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Verifies that the incoming signature matches the HMAC-SHA256 of the raw payload.
     * Uses constant-time comparison to prevent timing attacks.
     */
    public boolean isValidSignature(String rawBody, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isBlank() || rawBody == null) {
            log.warn("Missing signature header or empty request body for SePay webhook");
            return false;
        }

        // Support headers like 'Bearer <sig>' or 'Apikey <sig>' or raw signature
        String cleanedSignature = signatureHeader;
        if (cleanedSignature.startsWith("Bearer ")) {
            cleanedSignature = cleanedSignature.substring(7).trim();
        } else if (cleanedSignature.startsWith("Apikey ")) {
            cleanedSignature = cleanedSignature.substring(7).trim();
        }

        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
            );
            hmacSha256.init(secretKeySpec);

            byte[] hmacBytes = hmacSha256.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            String calculatedHex = HexFormat.of().formatHex(hmacBytes);

            // Constant-time comparison
            boolean matchesHex = MessageDigest.isEqual(
                    calculatedHex.getBytes(StandardCharsets.UTF_8),
                    cleanedSignature.toLowerCase().getBytes(StandardCharsets.UTF_8)
            );

            // Also check direct API key equality if the user configured API Key mode
            boolean matchesDirectKey = MessageDigest.isEqual(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    cleanedSignature.getBytes(StandardCharsets.UTF_8)
            );

            return matchesHex || matchesDirectKey;

        } catch (Exception e) {
            log.error("Failed to compute HMAC-SHA256 for SePay webhook: {}", e.getMessage(), e);
            return false;
        }
    }

}
