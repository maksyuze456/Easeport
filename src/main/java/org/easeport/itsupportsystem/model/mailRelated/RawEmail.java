package org.easeport.itsupportsystem.model.mailRelated;

import java.time.LocalDateTime;

public class RawEmail {
    private String subject;
    private String from;
    private String content;
    private String messageId;

    private LocalDateTime localDateTime;

    public RawEmail(String subject, String from, String content, String messageId, LocalDateTime localDateTime) {
        this.subject = subject;
        this.from = from;
        this.content = content;
        this.messageId = messageId;
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public RawEmail() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        if (from == null) return null;
        int start = from.indexOf('<');
        int end = from.indexOf('>');
        if (start != -1 && end != -1 && end > start) {
            return from.substring(start + 1, end).trim();
        }
        return from.trim();
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
