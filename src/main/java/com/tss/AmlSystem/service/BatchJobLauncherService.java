package com.tss.AmlSystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchJobLauncherService {

    private final JobOperator jobLauncher;
    private final Job customerImportJob;

    public JobExecution launchCustomerJob(String filePath, Long fileId) throws Exception {
        org.springframework.batch.core.job.parameters.JobParameters params = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("fileId", fileId)
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.start(customerImportJob, params);
        log.info("Launched customer import job for fileId: {}", fileId);
        return jobExecution;
    }
}
