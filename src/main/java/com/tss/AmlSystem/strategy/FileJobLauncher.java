package com.tss.AmlSystem.strategy;

import com.tss.AmlSystem.entity.enums.tenant.FileType;

public interface FileJobLauncher {
    FileType getFileType();
    void launch(String filePath,Long fileId,String tenant) throws Exception;
}
