/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package util;

import jakarta.annotation.Resource;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 *
 * @author tejan
 */
@Stateless
public class EmailService implements EmailServiceLocal {

    @Resource(name = "mail/MyMail")
    private Session mailSession;
    
    @Override
    @Asynchronous  
    public void sendEmail(String toEmail, String subject, String messageText) {
        try {
            if (toEmail == null || toEmail.trim().isEmpty()) {
                System.out.println("Email not sent: Invalid recipient");
                return;
            }

            // Create message
            Message message = new MimeMessage(mailSession);

            // Dynamic sender (from mail config)
            String fromEmail = mailSession.getProperty("mail.smtp.user");
            message.setFrom(new InternetAddress(fromEmail));

            // Receiver
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Subject
            message.setSubject(subject);

            // Content (TEXT)
            message.setText(messageText);

            // Send email
            Transport.send(message);

            System.out.println("Email Sent Successfully From: " + fromEmail);
            System.out.println("Email Sent Successfully to: " + toEmail);

        } catch (Exception e) {
            System.out.println("Email Sending Failed");
            e.printStackTrace();
        }
    }
}
