package com.tss.AmlSystem.strategy.batch.file;

import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.FileValidationErrorsRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.internal.Abstract;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
@Service
@Abstract
public abstract class FileValidator {
    protected final FileRepository fileRepository;
    protected final FileValidationErrorsRepository fileValidationErrorsRepository;

    abstract public FileType getFileType();
//    abstract public List<FileValidationErrors> validate(Object dto, File file, int rowNumber);


    public void saveValidationErrors(List<FileValidationErrors> errors) {
        if (errors != null && !errors.isEmpty()) {
            fileValidationErrorsRepository.saveAll(errors);
        }
    }

    public File getFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
    }

    protected void validateRequired(
            String value,
            String field,
            String message,
            File file,
            int rowNumber,
            List<FileValidationErrors> errors
    ) {
        if (!StringUtils.hasText(value)) {
            errors.add(buildError(file, rowNumber, field, message));
        }
    }

    protected void validateDecimalNotNegative(
            String value,
            String field,
            String message,
            File file,
            int rowNumber,
            List<FileValidationErrors> errors
    ) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        BigDecimal number;
        try {
            number = new BigDecimal(value.trim());
            if(number.compareTo(BigDecimal.ZERO) < 0) {
                errors.add(buildError(file, rowNumber, field,  "Value cannot be negative"));
            }
        } catch (NumberFormatException ex) {
            errors.add(buildError(file, rowNumber, field, message));
        }

    }

    protected FileValidationErrors buildError(File file, int rowNumber, String field, String message) {
        FileValidationErrors error = new FileValidationErrors();
        error.setFile(file);
        error.setRowNumber(rowNumber);
        error.setFieldName(field);
        error.setErrorMessage(message);
        return error;
    }
}
