package com.tss.AmlSystem.batch.listener;

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
        Long fileId = jobExecution.getJobParameters().getLong("fileId");
        if (fileId == null) {
            return;
        }

        fileRepository.findById(fileId).ifPresent(file -> {
            Optional<StepExecution> stepExecution = jobExecution.getStepExecutions().stream().findFirst();
            long written = stepExecution.map(StepExecution::getWriteCount).orElse(0l);
            long failed = stepExecution.map(step ->
                    step.getFilterCount() + step.getReadSkipCount() + step.getProcessSkipCount() + step.getWriteSkipCount()
            ).orElse(0l);

            file.setSuccessRecords((int) written);
            file.setFailedRecords((int) failed);
            file.setProcessedAt(LocalDateTime.now());

            file.setStatus(BatchStatus.COMPLETED);

            fileRepository.save(file);
            log.info("File {} done - written: {}, failed: {}", fileId, written, failed);
        });
    }
}
