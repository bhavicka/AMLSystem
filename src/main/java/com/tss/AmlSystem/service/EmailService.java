package com.tss.AmlSystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendRegistrationEmail(String to, String name, String tempPass) {
        log.info("{} Preparing to send registration email to: {}", LogTag.EMAIL.getValue(), to);
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("password", tempPass);

            String html = templateEngine.process("emails/registration", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("AML System - Account Created");
            helper.setText(html, true);

            mailSender.send(message);
            log.info("{} Registration email sent successfully to: {}", LogTag.EMAIL.getValue(), to);
        } catch (MessagingException e) {
            log.error("{} Failed to send email to {}: {}", LogTag.EMAIL.getValue(), to, e.getMessage());
            throw new IllegalStateException("Failed to send email");
        }
    }
}