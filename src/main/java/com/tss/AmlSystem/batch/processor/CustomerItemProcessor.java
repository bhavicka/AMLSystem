package com.tss.AmlSystem.batch.processor;

import com.tss.AmlSystem.dto.request.CustomerDTO;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.OccupationType;
import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.FileValidationErrorsRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@StepScope
public class CustomerItemProcessor implements ItemProcessor<CustomerDTO, Customer> {

    private final FileValidationErrorsRepository errorRepository;

    // fileId is injected from job parameters at runtime
    private FileRepository fileRepository;
    private File fileEntity;

    @Value("#{jobParameters['fileId']}")
    public void setFileId(Long fileId) {
        this.fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));
    }

    @Override
    public Customer process(CustomerDTO dto) {
        List<FileValidationErrors> errors = validate(dto);

        if (!errors.isEmpty()) {
            errorRepository.saveAll(errors); // log to DB
            return null; // skip this row — Spring Batch won't pass null to Writer
        }

        return convertToEntity(dto); // valid → convert and send to Writer
    }

    private List<FileValidationErrors> validate(CustomerDTO dto) {
        List<FileValidationErrors> errors = new ArrayList<>();
        int row = 0; // we'll handle row tracking shortly

        if (StringUtils.isBlank(dto.getClientNumber()))
            errors.add(error("client_number", "Required field missing"));

        if (!dto.getAadharNumber().matches("\\d{12}"))
            errors.add(error("aadhar_number", "Must be 12 digits"));

        if (!dto.getPan().matches("[A-Z]{5}[0-9]{4}[A-Z]"))
            errors.add(error("pan", "Invalid PAN format"));

        try { OccupationType.valueOf(dto.getOccupationType()); }
        catch (Exception e) { errors.add(error("occupation_type", "Invalid value")); }

        try { Severity.valueOf(dto.getRiskRate()); }
        catch (Exception e) { errors.add(error("risk_rate", "Invalid value")); }

        try { new BigDecimal(dto.getMonthlyIncome()); }
        catch (Exception e) { errors.add(error("monthly_income", "Invalid number")); }

        try { LocalDate.parse(dto.getDob()); }
        catch (Exception e) { errors.add(error("dob", "Invalid date, use yyyy-MM-dd")); }

        return errors;
    }

    private FileValidationErrors error(String field, String message) {
        FileValidationErrors e = new FileValidationErrors();
        e.setFile(fileEntity);
        e.setFieldName(field);
        e.setErrorMessage(message);
        return e;
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer c = new Customer();
        c.setClientNumber(dto.getClientNumber());
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setMiddleName(dto.getMiddleName());
        c.setAadharNumber(dto.getAadharNumber());
        c.setPan(dto.getPan());
        c.setOccupation(dto.getOccupation());
        c.setOccupationType(OccupationType.valueOf(dto.getOccupationType()));
        c.setIsPep(Boolean.parseBoolean(dto.getIsPep()));
        c.setRiskRate(Severity.valueOf(dto.getRiskRate()));
        c.setMonthlyIncome(new BigDecimal(dto.getMonthlyIncome()));
        c.setDob(LocalDate.parse(dto.getDob()));
        if (StringUtils.isNotBlank(dto.getProfessionMultiplier()))
            c.setProfessionMultiplier(new BigDecimal(dto.getProfessionMultiplier()));
        c.setFamilyCode(dto.getFamilyCode());
        return c;
    }
}
