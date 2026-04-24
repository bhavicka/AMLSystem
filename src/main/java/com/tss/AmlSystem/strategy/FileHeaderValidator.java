package com.tss.AmlSystem.strategy;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileHeaderValidator {
    FileType getFileType();
    void validate(MultipartFile multipartFile) throws IOException;
}
