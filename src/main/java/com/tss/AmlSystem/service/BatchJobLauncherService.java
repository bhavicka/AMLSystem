package com.tss.AmlSystem.service;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BatchJobLauncherService {

    private final JobLauncher jobLauncher;
    private final Job customerImportJob;
    private final Job accountImportJob;
    private final Job transactionImportJob;

    public BatchJobLauncherService(
            JobLauncher jobLauncher,
            @Qualifier("customerImportJob") Job customerImportJob,
            @Qualifier("accountImportJob") Job accountImportJob,
            @Qualifier("transactionImportJob") Job transactionImportJob
    ) {
        this.jobLauncher = jobLauncher;
        this.customerImportJob = customerImportJob;
        this.accountImportJob = accountImportJob;
        this.transactionImportJob = transactionImportJob;
    }

    @Async
    public void launchCustomerJob(String filePath, Long fileId, String tenant) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("fileId", fileId)
                .addString("tenant", tenant)
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(customerImportJob, params);
    }

    @Async
    public void launchAccountJob(String filePath, Long fileId, String tenant) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("fileId", fileId)
                .addString("tenant", tenant)
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(accountImportJob, params);
    }
    @Async
    public void launchTransactionJob(String filePath, Long fileId, String tenant) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("fileId", fileId)
                .addString("tenant", tenant)
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(transactionImportJob, params);
    }
}
