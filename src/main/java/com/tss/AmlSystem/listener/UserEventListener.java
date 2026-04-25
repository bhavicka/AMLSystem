package com.tss.AmlSystem.listener;

import com.tss.AmlSystem.dto.event.UserRegisteredEvent;
import com.tss.AmlSystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @EventListener
    @Async // This ensures the email sending doesn't slow down the registration response
    public void handleUserRegistration(UserRegisteredEvent event) {
        emailService.sendRegistrationEmail(
                event.email(),
                event.name(),
                event.tempPassword()
        );
    }
}