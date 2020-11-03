package dev.hotdeals.treecreate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService
{
    @Qualifier("getJavaInfoMailSender")
    @Autowired
    private JavaMailSender infoEmailSender;

    @Qualifier("getJavaOrderMailSender")
    @Autowired
    private JavaMailSender orderEmailSender;

    public void sendInfoMail(String emailText, String emailSubject, String to) throws MailException
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("info@treecreate.dk");
        message.setTo(to);
        message.setSubject(emailSubject);
        message.setText(emailText);
        infoEmailSender.send(message);
    }

    public void sendOrderMail(String emailText, String emailSubject, String to) throws MailException
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orders@treecreate.dk");
        message.setTo(to);
        message.setSubject(emailSubject);
        message.setText(emailText);
        orderEmailSender.send(message);
    }
}
