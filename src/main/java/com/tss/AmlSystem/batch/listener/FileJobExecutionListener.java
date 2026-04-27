package com.tss.AmlSystem.batch.listener;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.entity.enums.tenant.FileStatus;
import com.tss.AmlSystem.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.stereotype.Component;

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
            file.setStatus(FileStatus.PROCESSING);
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
                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    file.setStatus(FileStatus.COMPLETED);
                    log.info("File {} processing completed successfully.", fileId);
                } else {
                    file.setStatus(FileStatus.FAILED);
                    log.error("File {} processing failed with status: {}", fileId, jobExecution.getStatus());
                    jobExecution.getAllFailureExceptions().forEach(e -> {
                        System.err.println("❌ Critical Job Failure Exception: " + e.getMessage());
                        e.printStackTrace();
                    });
                }
                fileRepository.save(file);
            });
        } finally {
            TenantContext.clear();
            log.info("Cleared TenantContext for jobExecutionId: {}", jobExecution.getId());
        }
    }
}
