package com.tss.AmlSystem.batch.processor;

import com.tss.AmlSystem.dto.request.CustomerDTO;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.OccupationType;
import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.service.FileValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@StepScope
public class CustomerItemProcessor implements ItemProcessor<CustomerDTO, Customer>, StepExecutionListener {

    private final FileValidationService fileValidationService;

    private File fileEntity;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        Long fileId = stepExecution.getJobParameters().getLong("fileId");
        this.fileEntity = fileValidationService.getFile(fileId);
    }

    @Override
    public Customer process(CustomerDTO dto) {
        int rowNumber = currentRowNumber();
        List<FileValidationErrors> errors = fileValidationService.validateCustomer(dto, fileEntity, rowNumber);

        if (!errors.isEmpty()) {
            fileValidationService.saveValidationErrors(errors);
            return null;
        }

        return convertToEntity(dto);
    }

    private int currentRowNumber() {
        StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
        return (int)(stepExecution.getReadCount() + 1);
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setClientNumber(dto.getClientNumber().trim());
        customer.setFirstName(dto.getFirstName().trim());
        customer.setLastName(dto.getLastName().trim());
        customer.setMiddleName(normalizeOptional(dto.getMiddleName()));
        customer.setAadharNumber(dto.getAadharNumber().trim());
        customer.setPan(dto.getPan().trim().toUpperCase(Locale.ROOT));
        customer.setOccupation(dto.getOccupation().trim());
        customer.setOccupationType(OccupationType.valueOf(dto.getOccupationType().trim().toUpperCase(Locale.ROOT)));
        customer.setIsPep(Boolean.parseBoolean(dto.getIsPep().trim()));
        customer.setRiskRate(Severity.valueOf(dto.getRiskRate().trim().toUpperCase(Locale.ROOT)));
        customer.setMonthlyIncome(new BigDecimal(dto.getMonthlyIncome().trim()));
        customer.setDob(LocalDate.parse(dto.getDob().trim()));

        if (StringUtils.hasText(dto.getProfessionMultiplier())) {
            customer.setProfessionMultiplier(new BigDecimal(dto.getProfessionMultiplier().trim()));
        }

        customer.setFamilyCode(normalizeOptional(dto.getFamilyCode()));
        return customer;
    }

    private String normalizeOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
