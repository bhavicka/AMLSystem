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

//    @Override
    public List<FileValidationErrors> validate(Object dtoObj, File file, int rowNumber) {
        if(!(dtoObj instanceof CustomerBatchProcessDto dto))
            throw new IllegalArgumentException("Expected CustomerBatchProcessDto but got " + dtoObj.getClass().getSimpleName());
        List<FileValidationErrors> errors = new ArrayList<>();

        validateRequired(dto.getClientNumber(), "client_number", "Client number is required", file, rowNumber, errors);
        validateRequired(dto.getFirstName(), "first_name", "First name is required", file, rowNumber, errors);
        validateRequired(dto.getLastName(), "last_name", "Last name is required", file, rowNumber, errors);
        validateRequired(dto.getAadharNumber(), "aadhar_number", "Aadhar number is required", file, rowNumber, errors);
        validateRequired(dto.getPan(), "pan", "PAN is required", file, rowNumber, errors);
        validateRequired(dto.getOccupation(), "occupation", "Occupation is required", file, rowNumber, errors);
        validateRequired(dto.getOccupationType(), "occupation_type", "Occupation type is required", file, rowNumber, errors);
        validateRequired(dto.getIsPep(), "is_pep", "PEP flag is required", file, rowNumber, errors);
        validateRequired(dto.getRiskRate(), "risk_rate", "Risk rate is required", file, rowNumber, errors);
        validateRequired(dto.getMonthlyIncome(), "monthly_income", "Monthly income is required", file, rowNumber, errors);
        validateRequired(dto.getDob(), "dob", "Date of birth is required", file, rowNumber, errors);

        if (StringUtils.hasText(dto.getAadharNumber()) && !dto.getAadharNumber().trim().matches("\\d{12}")) {
            errors.add(buildError(file, rowNumber, "aadhar_number", "Aadhar number must contain exactly 12 digits"));
        }

        if (StringUtils.hasText(dto.getPan()) && !dto.getPan().trim().toUpperCase(Locale.ROOT).matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
            errors.add(buildError(file, rowNumber, "pan", "PAN format is invalid"));
        }

        if (StringUtils.hasText(dto.getOccupationType())) {
            try {
                OccupationType.valueOf(dto.getOccupationType().trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                errors.add(buildError(file, rowNumber, "occupation_type", "Invalid occupation type"));
            }
        }

        if (StringUtils.hasText(dto.getRiskRate())) {
            try {
                Severity.valueOf(dto.getRiskRate().trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                errors.add(buildError(file, rowNumber, "risk_rate", "Invalid risk rate"));
            }
        }

        if (StringUtils.hasText(dto.getIsPep())) {
            String value = dto.getIsPep().trim();
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                errors.add(buildError(file, rowNumber, "is_pep", "PEP flag must be true or false"));
            }
        }

        validateDecimalNotNegative(dto.getMonthlyIncome(), "monthly_income", "Monthly income must be a valid number", file, rowNumber, errors);

        if (StringUtils.hasText(dto.getProfessionMultiplier())) {
            validateDecimalNotNegative(dto.getProfessionMultiplier(), "profession_multiplier", "Profession multiplier must be a valid number", file, rowNumber, errors);
        }

        if (StringUtils.hasText(dto.getDob())) {
            try {
                LocalDate.parse(dto.getDob().trim());
            } catch (DateTimeParseException ex) {
                errors.add(buildError(file, rowNumber, "dob", "Date of birth must use yyyy-MM-dd format"));
            }
        }

        return errors;
    }

    @Override
    public FileType getFileType() {
        return FileType.CUSTOMERS;
    }
}
