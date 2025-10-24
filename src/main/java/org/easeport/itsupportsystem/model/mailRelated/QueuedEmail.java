package org.easeport.itsupportsystem.model.mailRelated;

public class QueuedEmail {
    private final RawEmail rawEmail;
    private final String inReplyTo;

    public QueuedEmail(RawEmail rawEmail, String inReplyTo) {
        this.rawEmail = rawEmail;
        this.inReplyTo = inReplyTo;
    }

    public RawEmail getRawEmail() {
        return rawEmail;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }
}