package com.tss.AmlSystem.strategy;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.service.BatchJobLauncherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerJobLauncher implements FileJobLauncher {
    private final BatchJobLauncherService batchJobLauncherService;

    @Override
    public FileType getFileType() {
        return FileType.CUSTOMERS;
    }

    @Override
    public Long launch(String filePath, Long fileId, String tenant) throws Exception {
        return batchJobLauncherService.launchCustomerJob(filePath, fileId, tenant);
    }
}
