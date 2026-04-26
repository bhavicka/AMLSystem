package com.tss.AmlSystem.strategy.batch.joblaunch;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.service.BatchJobLauncherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionJobLauncher implements FileJobLauncher {
    private final BatchJobLauncherService batchJobLauncherService;

    @Override
    public FileType getFileType() {
        return FileType.TRANSACTIONS;
    }

    @Override
    public void launch(String filePath, Long fileId, String tenant) throws Exception {
        batchJobLauncherService.launchTransactionJob(filePath, fileId, tenant);
    }
}