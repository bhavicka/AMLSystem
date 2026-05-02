package com.tss.AmlSystem.service;

import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.repository.AccountRepository;
import com.tss.AmlSystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final PdfGenerationService pdfGenerationService;

    public List<Transaction> generateTransactionHistoryPdf(String customerNumber){
        List<Account> accountList = accountRepository.findByClientNumber(customerNumber);
        List<Transaction> transactionList = new ArrayList<>();
        for(Account account: accountList){
            String accountNumber = account.getAccountNumber();
            transactionList.addAll(transactionRepository.findByAccountNumber(accountNumber));
            transactionList.addAll(transactionRepository.findByCounterPartyAccountNumber(accountNumber));
        }
        return transactionList;
    }
}
