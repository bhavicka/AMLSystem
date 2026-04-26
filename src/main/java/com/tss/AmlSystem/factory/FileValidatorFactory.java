package com.tss.AmlSystem.factory;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.strategy.batch.file.FileValidator;
import com.tss.AmlSystem.strategy.batch.header.FileHeaderValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileValidatorFactory {
    private final Map<FileType, FileValidator> fileValidatorMap;

    public FileValidatorFactory(List<FileValidator> validators) {
        this.fileValidatorMap = validators.stream()
                .collect(Collectors.toMap(FileValidator::getFileType, v -> v));
    }

    public FileValidator getValidator(FileType fileType) {
        FileValidator validator = fileValidatorMap.get(fileType);
        if (validator == null) {
            throw new IllegalArgumentException("No validator found for " + fileType);
        }
        return validator;
    }
}
