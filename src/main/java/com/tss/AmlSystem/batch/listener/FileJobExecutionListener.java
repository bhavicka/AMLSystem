package com.tss.AmlSystem.batch.listener;

import com.tss.AmlSystem.entity.enums.tenant.BatchStatus;
import com.tss.AmlSystem.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileJobExecutionListener implements JobExecutionListener {

    private final FileRepository fileRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        Long fileId = jobExecution.getJobParameters().getLong("fileId");

        fileRepository.findById(fileId).ifPresent(file -> {
            // Spring Batch tracks these counts automatically
            StepExecution step = jobExecution.getStepExecutions().iterator().next();

            long written = step.getWriteCount();  // successfully saved rows
            long skipped = step.getProcessSkipCount(); // rows processor returned null for

            file.setSuccessRecords((int) written);
            file.setFailedRecords((int) skipped);  // add this field to your File entity
            file.setProcessedAt(LocalDateTime.now());

            if (jobExecution.getStatus().equals(BatchStatus.COMPLETED) && skipped == 0) {
                file.setStatus(BatchStatus.COMPLETED);
            } else if (written > 0) {
                file.setStatus(BatchStatus.PARTIALLY_COMPLETED);
            } else {
                file.setStatus(BatchStatus.FAILED);
            }

            fileRepository.save(file);
            log.info("File {} done — written: {}, skipped: {}", fileId, written, skipped);
        });
    }
}
