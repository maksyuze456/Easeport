package org.easeport.itsupportsystem.config;

import jakarta.mail.Flags;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;

import java.util.Properties;

@Configuration
public class MailIntegrationConfig {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    // IMAP Mail Receiver - IMAP Client
    @Bean
    public ImapMailReceiver imapMailReceiver() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", "imap.gmail.com");
        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.imaps.ssl.enable", "true");

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
        receiver.setSearchTermStrategy((folder, msg) -> new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        return receiver;
    }


    // MailReceivingMessageSource (Inbound Adapter)
    @Bean
    @InboundChannelAdapter(channel = "emailChannel", poller = @Poller(fixedDelay = "30000"))
    public MailReceivingMessageSource mailMessageSource(ImapMailReceiver receiver) {
        return new MailReceivingMessageSource(receiver);
    }

    // Service Activator – process emails
    @ServiceActivator(inputChannel = "emailChannel")
    public void processEmail(MimeMessage email) throws Exception {



    }
}
