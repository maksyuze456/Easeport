package org.easeport.itsupportsystem.logging.service;

import org.easeport.itsupportsystem.logging.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Service
public class AuditLogger {

    private static final Logger AUDIT =
            LoggerFactory.getLogger("AUDIT");

    public void log(AuditEvent event) {
        AUDIT.info("audit_event {} {} {} {} {} {}",
                keyValue("actor", event.actor()),
                keyValue("action", event.action()),
                keyValue("target", event.target()),
                keyValue("outcome", event.outcome()),
                keyValue("correlationId", event.correlationId()),
                keyValue("sourceIp", event.sourceIp())
        );
    }
}
