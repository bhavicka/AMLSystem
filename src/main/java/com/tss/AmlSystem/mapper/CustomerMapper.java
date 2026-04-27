package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.CustomerBatchProcessDto;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.OccupationType;
import com.tss.AmlSystem.entity.tenant.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Mapper(
        componentModel = "spring",
        imports = {
                OccupationType.class,
                Severity.class,
                Locale.class,
                BigDecimal.class,
                LocalDate.class
        }
)
public interface CustomerMapper {

    @Mapping(target = "clientNumber", expression = "java(processDto.clientNumber().trim())")
    @Mapping(target = "firstName", expression = "java(processDto.firstName().trim())")
    @Mapping(target = "lastName", expression = "java(processDto.lastName().trim())")
    @Mapping(target = "middleName", expression = "java(processDto.middleName() != null ? processDto.middleName().trim() : null)")
    @Mapping(target = "aadharNumber", expression = "java(processDto.aadharNumber().trim())")
    @Mapping(target = "pan", expression = "java(processDto.pan().trim().toUpperCase(Locale.ROOT))")
    @Mapping(target = "occupation", expression = "java(processDto.occupation().trim())")
    @Mapping(target = "occupationType", expression = "java(OccupationType.valueOf(processDto.occupationType().trim().toUpperCase(Locale.ROOT)))")
    @Mapping(target = "riskRate", expression = "java(Severity.valueOf(processDto.riskRate().trim().toUpperCase(Locale.ROOT)))")
    @Mapping(target = "isPep", expression = "java(Boolean.parseBoolean(processDto.isPep().trim()))")
    @Mapping(target = "familyCode", expression = "java(processDto.familyCode() != null ? processDto.familyCode().trim() : null)")
    @Mapping(target = "dob", source = "dob")
    @Mapping(target = "monthlyIncome", source = "monthlyIncome")
    @Mapping(target = "professionMultiplier", source = "professionMultiplier")
    Customer toCustomer(CustomerBatchProcessDto processDto);

    default LocalDate mapStringToLocalDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateString.trim(), formatter);
    }
    default BigDecimal mapToBigDecimal(String amount) {
        if (amount == null || amount.isBlank()) {
            return null;
        }
        try {
            String cleanedAmount = amount.trim().replace(",", "");
            return new BigDecimal(cleanedAmount);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
