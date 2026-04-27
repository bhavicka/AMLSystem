package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.TransactionBatchProcessDto;
import com.tss.AmlSystem.entity.enums.tenant.TransactionMode;
import com.tss.AmlSystem.entity.enums.tenant.TransactionType;
import com.tss.AmlSystem.entity.tenant.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.transaction.event.TransactionalApplicationListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Mapper(
        componentModel = "spring",
        imports = {
                TransactionType.class,
                TransactionMode.class,
                Locale.class,
                BigDecimal.class,
                LocalDate.class,
                DateTimeFormatter.class
        }
)
public interface TransactionMapper {
    @Mapping(target = "accountNumber", expression = "java(processDto.getAccountNumber().trim())")
    @Mapping(target = "counterPartyAccountNumber",
            expression = "java(processDto.getCounterPartyAccountNumber() != null ? processDto.getCounterPartyAccountNumber().trim() : null)")
    @Mapping(target = "transactionType", expression = "java(TransactionType.valueOf(processDto.getTransactionType().trim().toUpperCase(Locale.ROOT)))")
    @Mapping(target = "transactionMode", expression = "java(TransactionMode.valueOf(processDto.getTransactionMode().trim().toUpperCase(Locale.ROOT)))")
    @Mapping(target = "transactionReferenceNumber", expression = "java(processDto.getTransactionReferenceNumber().trim())")
    @Mapping(target = "transactionDate", source = "transactionDate")
    @Mapping(target = "amount", source = "amount")
    Transaction toTransaction(TransactionBatchProcessDto processDto);

    default LocalDate mapStringToLocalDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        // It's safer to wrap this in a try-catch even with Regex validation
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(dateString.trim(), formatter);
        } catch (Exception e) {
            return null;
        }
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

