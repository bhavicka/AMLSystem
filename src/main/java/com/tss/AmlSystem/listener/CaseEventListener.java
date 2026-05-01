package com.tss.AmlSystem.listener;

import com.tss.AmlSystem.dto.event.CaseCreatedEvent;
import com.tss.AmlSystem.dto.event.CaseEscalatedEvent;
import com.tss.AmlSystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CaseEventListener {
    private final EmailService emailService;

    @EventListener
    @Async
    public void handleCaseCreation(CaseCreatedEvent event) {
        emailService.sendCaseCreationEmail(
                event.officerEmail(),
                event.officerName(),
                event.caseReferenceNumber()
        );
    }

    @EventListener
    @Async
    public void handleCaseEscalation(CaseEscalatedEvent event) {
        emailService.sendCaseEscalationEmail(
                event.bankAdminEmail(),
                event.bankAdminName(),
                event.caseReferenceNumber()
        );
    }
}
