package com.example.springtour.config.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditEventListener {

    private final AuditUtils auditUtils;

    public AuditEventListener(AuditUtils auditUtils) {
        this.auditUtils = auditUtils;
    }

    @Async("auditExecutor")
    @EventListener
    public void handleAuditEvent(AuditEvent event) {
        AuditEntry entry = event.getAuditEntry();
        auditUtils.writeAudit(entry);
    }
}
