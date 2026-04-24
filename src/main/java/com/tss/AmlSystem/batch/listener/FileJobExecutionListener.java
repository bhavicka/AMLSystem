package com.tss.AmlSystem.batch.listener;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.entity.enums.tenant.BatchStatus;
import com.tss.AmlSystem.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileJobExecutionListener implements JobExecutionListener {

    private final FileRepository fileRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String tenant = jobExecution.getJobParameters().getString("tenant");
        if (tenant != null) {
            TenantContext.setCurrentTenant(tenant);
            log.info("Set TenantContext to: {} for jobExecutionId: {}", tenant, jobExecution.getId());
        }

        Long fileId = jobExecution.getJobParameters().getLong("fileId");
        if (fileId == null) {
            return;
        }

        fileRepository.findById(fileId).ifPresent(file -> {
            file.setStatus(BatchStatus.PROCESSING);
            fileRepository.save(file);
        });
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            Long fileId = jobExecution.getJobParameters().getLong("fileId");
            if (fileId == null) {
                return;
            }

            fileRepository.findById(fileId).ifPresent(file -> {
                file.setStatus(BatchStatus.COMPLETED);
                fileRepository.save(file);
                log.info("File {} processing completed.", fileId);
            });
        } finally {
            TenantContext.clear();
            log.info("Cleared TenantContext for jobExecutionId: {}", jobExecution.getId());
        }
    }
}
