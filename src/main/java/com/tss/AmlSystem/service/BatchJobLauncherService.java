package com.tss.AmlSystem.service;

import com.tss.AmlSystem.batch.config.CustomerBatchConfig;
import com.tss.AmlSystem.batch.processor.CustomerItemProcessor;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BatchJobLauncherService {

     private JobOperator jobOperator;
     private Job customerImportJob;

    public void launchCustomerJob(String filePath, Long fileId) throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addLong("fileId", fileId)
                .addLong("startTime", System.currentTimeMillis()) // ensures uniqueness
                .toJobParameters();

        jobOperator.start(customerImportJob, params);
        log.info("Launched customer import job for fileId: {}", fileId);
    }
}