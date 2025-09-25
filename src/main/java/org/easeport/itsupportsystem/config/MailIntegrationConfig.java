package org.easeport.itsupportsystem.config;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.Message;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class MailIntegrationConfig {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    private final BlockingQueue<RawEmail> emailQueue;

    public MailIntegrationConfig(BlockingQueue<RawEmail> emailQueue) {
        this.emailQueue = emailQueue;
    }


    // IMAP Mail Receiver - IMAP Client
    @Bean
    public ImapMailReceiver imapMailReceiver() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", "imap.gmail.com");
        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.imaps.ssl.enable", "true");
        props.setProperty("mail.imaps.connectionpoolsize", "1");
        props.setProperty("mail.imaps.connectionpooltimeout", "300000");
        props.setProperty("mail.imaps.fetchsize", "1048576"); // 1MB
        props.setProperty("mail.imaps.partialfetch", "false"); // Fetch complete message
        props.setProperty("mail.imaps.peek", "true"); // Don't mark as read while fetching

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        ImapMailReceiver receiver = new ImapMailReceiver("imaps://imap.gmail.com:993/INBOX");
        receiver.setSession(session);
        receiver.setShouldDeleteMessages(false);
        receiver.setShouldMarkMessagesAsRead(true);
        receiver.setSimpleContent(true);
        receiver.setSearchTermStrategy((folder, msg) -> new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        return receiver;
    }


    // MailReceivingMessageSource (Inbound Adapter)
    @Bean
    @InboundChannelAdapter(channel = "emailChannel", poller = @Poller(fixedDelay = "1000"))
    public MailReceivingMessageSource mailMessageSource(ImapMailReceiver receiver) {
        return new MailReceivingMessageSource(receiver);
    }

    // Service Activator – process emails
    @ServiceActivator(inputChannel = "emailChannel")
    public void processEmail(Message<MimeMessage> message) throws Exception {
        MimeMessage email = message.getPayload();
        Object emailContent = email.getContent();
        String subject = email.getSubject();
        String from = email.getFrom()[0].toString();
        String messageId = email.getMessageID();
        String content = "";

        if (emailContent instanceof String) {
            content = (String) emailContent;
        } else if (emailContent instanceof MimeMultipart) {
            content = extractTextFromMultipart((MimeMultipart) emailContent);
        }
        System.out.println("=== EMAIL RECEIVED ===");
        System.out.println("Subject: " + subject);
        System.out.println("From: " + from);
        System.out.println("Content: " + content);
        System.out.println("Message ID: " + messageId);
        System.out.println("=====================");
        RawEmail rawEmail = new RawEmail(subject, from, content, messageId);
        boolean pooled = emailQueue.offer(rawEmail);
        System.out.println(pooled);

    }

    private String extractTextFromMultipart(MimeMultipart multipart) throws Exception{
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.isMimeType("text/plain")) {
                result.append(part.getContent());
            }
        }
        return result.toString();
    }

}
