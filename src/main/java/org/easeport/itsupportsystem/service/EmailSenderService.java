package org.easeport.itsupportsystem.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.easeport.itsupportsystem.model.Ticket;
import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final Session smtpSession;

    @Value("${spring.mail.username}")
    String username;
    public EmailSenderService(@Qualifier("smtpSession") Session smtpSession) {
        this.smtpSession = smtpSession;
    }

    public void sendMail(Ticket ticket) {
        try {
            MimeMessage message = new MimeMessage(smtpSession);
            message.setFrom(new InternetAddress("easeport.tickets@gmail.com"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(ticket.getFrom()));
            message.setSubject(ticket.getSubject());
            message.setText(buildEnglishReplyContent(ticket));

            if(ticket.getMessageId() != null) {
                message.setHeader("In-Reply-To", ticket.getMessageId());
                message.setHeader("References", ticket.getMessageId());
            }

            Transport.send(message);
            System.out.println("Mail sent to " + ticket.getFrom());

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildEnglishReplyContent(Ticket ticket) {
        StringBuilder content = new StringBuilder();

        content.append("Dear ").append(ticket.getName() != null ? ticket.getName(): "Customer").append(".\n\n");

        content.append(ticket.getAnswer());

        content.append("\n\n");
        content.append("Best regards, \n");
        content.append("Easeport, \n");
        content.append("Support team\n");
        content.append("For reference, ticket ID: " + ticket.getId());

        return content.toString();
    }

    public String sendAnswer(TicketMessage ticketMessage, Ticket ticket) {

        try{
            MimeMessage message = new MimeMessage(smtpSession);

            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(ticket.getFrom()));
            message.setSubject("Re: " + ticket.getSubject());
            message.setText(buildEnglishReplyContent(ticket));

            if(ticketMessage.getInReplyTo() != null) {
                message.setHeader("In-Reply-To", ticketMessage.getInReplyTo());
                message.setHeader("References", ticketMessage.getInReplyTo());
            }
            Transport.send(message);
            String messageId = message.getMessageID();

            return messageId;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
