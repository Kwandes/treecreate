package dev.hotdeals.treecreate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService
{
    @Qualifier("getJavaInfoMailSender")
    @Autowired
    private JavaMailSender infoEmailSender;

    @Qualifier("getJavaOrderMailSender")
    @Autowired
    private JavaMailSender orderEmailSender;

    public void sendInfoMail(String emailText, String emailSubject, String to) throws MailException, MessagingException
    {
        MimeMessage mimeMessage = infoEmailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        mimeMessage.setContent(emailText, "text/html"); /* Use this or below line */
        //helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(to);
        helper.setSubject(emailSubject);
        helper.setFrom("Treecreate" + "<" +"info@treecreate.dk" + ">");
        infoEmailSender.send(mimeMessage);
    }

    public void sendOrderMail(String emailText, String emailSubject, String to) throws MailException, MessagingException
    {
        MimeMessage mimeMessage = orderEmailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        mimeMessage.setContent(emailText, "text/html"); /* Use this or below line */
        //helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(to);
        helper.setSubject(emailSubject);
        helper.setFrom("Treecreate" + "<" + "orders@treecreate.dk" + ">");
        orderEmailSender.send(mimeMessage);
    }
}
