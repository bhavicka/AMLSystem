package com.tss.AmlSystem.strategy.batch.header;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.exception.BusinessValidationException;
import com.tss.AmlSystem.utils.FileHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Service
public class TransactionHeaderValidator implements FileHeaderValidator {
    @Override
    public FileType getFileType() {
        return FileType.TRANSACTIONS;
    }
    @Override
    public void validate(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String headerLine = reader.readLine();

            if (headerLine == null) {
                throw new BusinessValidationException("Empty file");
            }

            List<String> actualHeaders = Arrays.stream(headerLine.split(","))
                    .map(String::trim)
                    .toList();

            List<String> expectedHeaders = FileHeaders.TRANSACTION_HEADER;

            if (!actualHeaders.equals(expectedHeaders)) {
                throw new BusinessValidationException("Invalid TRANSACTIONS file headers");
            }
        }
    }
}

