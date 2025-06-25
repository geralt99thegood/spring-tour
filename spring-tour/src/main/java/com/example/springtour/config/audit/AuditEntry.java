package com.example.springtour.config.audit;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Builder
@Data
public class AuditEntry {
        private String service;
        private String username;
        private Instant timestamp;
        private String direction; // IN / OUT
        private String method;
        private String url;
        private Map<String, String> headers;
        private String requestBody;
        private String responseBody;
        private int responseStatus;
        private long duration;
        private String error;

}
