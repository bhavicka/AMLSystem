package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.OccupationType;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.FileValidationErrorsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FileValidationService {

    private final FileRepository fileRepository;
    private final FileValidationErrorsRepository fileValidationErrorsRepository;

    public List<FileValidationErrors> validateCustomer(CustomerBatchProcessDto dto, File file, int rowNumber) {
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

        validateDecimal(dto.getMonthlyIncome(), "monthly_income", "Monthly income must be a valid number", file, rowNumber, errors);

        if (StringUtils.hasText(dto.getProfessionMultiplier())) {
            validateDecimal(dto.getProfessionMultiplier(), "profession_multiplier", "Profession multiplier must be a valid number", file, rowNumber, errors);
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

    public void saveValidationErrors(List<FileValidationErrors> errors) {
        if (!errors.isEmpty()) {
            fileValidationErrorsRepository.saveAll(errors);
        }
    }

    public File getFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
    }

    private void validateRequired(
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

    private void validateDecimal(
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

    private FileValidationErrors buildError(File file, int rowNumber, String field, String message) {
        FileValidationErrors error = new FileValidationErrors();
        error.setFile(file);
        error.setRowNumber(rowNumber);
        error.setFieldName(field);
        error.setErrorMessage(message);
        return error;
    }
}
