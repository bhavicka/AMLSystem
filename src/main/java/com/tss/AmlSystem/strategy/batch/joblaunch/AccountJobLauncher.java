package com.tss.AmlSystem.strategy.batch.joblaunch;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.service.BatchJobLauncherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountJobLauncher implements FileJobLauncher {
    private final BatchJobLauncherService batchJobLauncherService;

    @Override
    public FileType getFileType() {
        return FileType.ACCOUNTS;
    }

    @Override
    public void launch(String filePath, Long fileId, String tenant) throws Exception {
        batchJobLauncherService.launchAccountJob(filePath, fileId, tenant);
    }
}
