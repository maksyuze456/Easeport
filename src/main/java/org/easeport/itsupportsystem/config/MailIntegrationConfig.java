package org.easeport.itsupportsystem.config;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.easeport.itsupportsystem.model.mailRelated.RawEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mail.ImapIdleChannelAdapter;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.messaging.Message;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

@Configuration
public class MailIntegrationConfig {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    // SMTP
    @Value("${spring.mail.smtp.host}")
    private String smtpHost;

    @Value("${spring.mail.smtp.port}")
    private String smtpPort;

    @Value("${spring.mail.smtp.username}")
    private String smtpUsername;

    @Value("${spring.mail.smtp.password}")
    private String smtpPassword;

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
        props.setProperty("mail.imaps.connectionpooltimeout", "100000");
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
        receiver.setAutoCloseFolder(true);

        return receiver;
    }

    @Bean
    public ImapIdleChannelAdapter imapIdleAdapter(ImapMailReceiver receiver) {
        ImapIdleChannelAdapter adapter = new ImapIdleChannelAdapter(receiver);
        adapter.setOutputChannelName("emailChannel");
        adapter.setAutoStartup(true);
        adapter.setShouldReconnectAutomatically(true);

        return adapter;
    }

    @Bean("smtpSession")
    public Session smtpSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

    }



    // Service Activator – process emails
    @ServiceActivator(inputChannel = "emailChannel")
    public void processEmail(Message<MimeMessage> message) throws Exception {
        MimeMessage email = message.getPayload();
        Object emailContent = email.getContent();
        String subject = email.getSubject();
        String from = email.getFrom()[0].toString();
        Date date = email.getReceivedDate();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

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
        System.out.println("Time: " + localDateTime.toString());
        System.out.println("Message ID: " + messageId);
        System.out.println("=====================");
        RawEmail rawEmail = new RawEmail(subject, from, content, messageId, localDateTime);
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
