package com.example.springtour.config.audit;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuditEvent extends ApplicationEvent {

    private final AuditEntry auditEntry;

    public AuditEvent(Object source, AuditEntry auditEntry) {
        super(source);
        this.auditEntry = auditEntry;
    }
}
