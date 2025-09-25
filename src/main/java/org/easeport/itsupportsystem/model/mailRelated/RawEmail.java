package org.easeport.itsupportsystem.model.mailRelated;

public class RawEmail {
    private String subject;
    private String from;
    private String content;
    private String messageId;

    public RawEmail(String subject, String from, String content, String messageId) {
        this.subject = subject;
        this.from = from;
        this.content = content;
        this.messageId = messageId;
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
        return from;
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
