package com.springboottayjv.demolearntayjv.controller;

import com.springboottayjv.demolearntayjv.dto.response.ResponseData;
import com.springboottayjv.demolearntayjv.dto.response.ResponseError;
import com.springboottayjv.demolearntayjv.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/common")
@Slf4j
@RequiredArgsConstructor
public class CommonController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseData<String> sendEmail(
            @RequestParam String receiver ,
            @RequestParam String subject ,
            @RequestParam String content ,
            @RequestParam(required = false) MultipartFile[] files
    ) {
        try {
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), emailService.sendEmail(receiver,subject,content,files));
        } catch (Exception e) {
            log.error("Sending email was failure , errorMessage={}",e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Sending was failure");
        }
    }

    @PostMapping("/send-email-100")
    public ResponseData<String> sendEmail100(
            @RequestParam String receiver ,
            @RequestParam String subject ,
            @RequestParam String content ,
            @RequestParam(required = false) MultipartFile[] files
    ) {
        for (int i = 0; i < 3; i++) {
            try {
                emailService.sendEmail(receiver, subject, content, files);
                log.info("Send email attempt number {}", i + 1);
            } catch (Exception e) {
                log.error("Sending email failed at attempt {}, errorMessage={}", i + 1, e.getMessage());
                return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Sending failed on attempt " + (i + 1));
            }
        }
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "All 100 emails sent successfully");
    }
    }

