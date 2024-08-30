package dev.shubham.emailservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shubham.emailservice.dtos.EmailDto;
import dev.shubham.emailservice.utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class EmailEventConsumer {

    private ObjectMapper objectMapper;
    public EmailEventConsumer(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendEmail",groupId = "sendEmail")
    public void handleEmailEvent(String message){
        try {
            EmailDto emailDto =  objectMapper.readValue(message, EmailDto.class);
            String from = emailDto.getFrom();
            String to = emailDto.getTo();
            String subject = emailDto.getSubject();
            String body = emailDto.getBody();

            //send to email;

            System.out.println("TLSEmail Start");
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, "eobwoiourzdgaumk"
                    );
                }
            };
            Session session = Session.getInstance(props, auth);

            EmailUtil.sendEmail(session, to,subject,body);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
