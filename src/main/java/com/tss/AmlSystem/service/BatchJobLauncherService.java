package com.tss.AmlSystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BatchJobLauncherService {

    private final JobLauncher jobLauncher;
    private final Job customerImportJob;

    public BatchJobLauncherService(JobLauncher jobLauncher, 
                                   @org.springframework.beans.factory.annotation.Qualifier("customerImportJob") Job customerImportJob) {
        this.jobLauncher = jobLauncher;
        this.customerImportJob = customerImportJob;
    }

    @Async
    public Long launchCustomerJob(String filePath, Long fileId, String tenant) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("fileId", fileId)
                .addString("tenant", tenant)
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(customerImportJob, params);
        log.info("Launched customer import job for fileId: {}, tenant: {}, executionId: {}", fileId, tenant, execution.getId());
        return execution.getId();
    }
}
