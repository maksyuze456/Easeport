package org.easeport.itsupportsystem.logging;

public record AuditEvent(
        String actor,
        String action,
        String target,
        String outcome,
        String correlationId,
        String sourceIp
) {}
