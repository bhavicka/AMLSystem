package com.tss.AmlSystem.factory;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.exception.BusinessValidationException;
import com.tss.AmlSystem.strategy.batch.header.FileHeaderValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileHeaderValidatorFactory {
    private final Map<FileType, FileHeaderValidator> validatorMap;

    public FileHeaderValidatorFactory(List<FileHeaderValidator> validators) {
        this.validatorMap = validators.stream()
                .collect(Collectors.toMap(FileHeaderValidator::getFileType, v -> v));
    }

    public FileHeaderValidator getValidator(FileType fileType) {
        FileHeaderValidator validator = validatorMap.get(fileType);
        if (validator == null) {
            throw new BusinessValidationException("No validator found for " + fileType);
        }
        return validator;
    }
}
