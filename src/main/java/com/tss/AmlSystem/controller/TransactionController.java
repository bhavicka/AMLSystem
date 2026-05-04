package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.service.PdfGenerationService;
import com.tss.AmlSystem.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final PdfGenerationService pdfGenerationService;

    @PreAuthorize("hasAuthority('COMPLIANCE_OFFICER') or hasAuthority('BANK_ADMIN')")
    @GetMapping(value = "/customers/{customerNumber}/transactions/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadTransactionPdf(@PathVariable String customerNumber) {
        List<Transaction> transactionList = transactionService.generateTransactionHistoryPdf(customerNumber);
        byte[] pdfBytes = pdfGenerationService.generateTransactionReport(transactionList, customerNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "transactions_" + customerNumber + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.setContentLength(pdfBytes.length);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }

}
