package com.tss.AmlSystem.strategy.batch.file;

import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.enums.tenant.OccupationType;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.FileValidationErrorsRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CustomerFileValidator extends FileValidator{

    public CustomerFileValidator(FileRepository fileRepository, FileValidationErrorsRepository fileValidationErrorsRepository) {
        super(fileRepository, fileValidationErrorsRepository);
    }

    @Override
    public FileType getFileType() {
        return FileType.CUSTOMERS;
    }
}
