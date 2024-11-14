package com.springboottayjv.demolearntayjv.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    public String sendEmail(String receiver, String subject, String content, MultipartFile[] files) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending email to " + receiver);
        MimeMessage Message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(Message,true,"UTF-8");
        helper.setFrom(emailFrom, "ANONYMOS TEAM" ); // TOGO gan ten
        if(receiver.contains(",")) {
            helper.setTo(InternetAddress.parse(receiver));
        }
        else {
            helper.setTo(receiver);
        }
        if(files != null) {
            for(MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()),file);
            }
        }
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(Message);
        log.info("Email has been send successfully , receiver = {}", receiver);
        return "sent";
    }

    public void sendConfirmLink(String emailTo, Long userId , String secretCode) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirm link to " + emailTo);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()  );

        Context context = new Context();

        String confirmLink= String.format("http://localhost:8081/user/confirm/%s?secretCode=%s",userId,secretCode);

        Map<String , Object> properties = new HashMap<>();
        properties.put("confirmLink", confirmLink);
        context.setVariables(properties);

        helper.setFrom(emailFrom,"Anonymos Team" );
        helper.setTo(emailTo);
        helper.setSubject("Your account was hacked by us");

        String html = templateEngine.process("confirm-email.html",context);
        helper.setText(html, true);

        mailSender.send(message);

        log.info("Email has been send successfully , emailTo = {}", emailTo);


    }

}
