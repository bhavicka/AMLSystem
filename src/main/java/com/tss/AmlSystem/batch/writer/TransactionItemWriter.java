package com.tss.AmlSystem.batch.writer;

import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.repository.AccountRepository;
import com.tss.AmlSystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionItemWriter implements ItemWriter<Transaction> {

    private final TransactionRepository transactionRepository;

    @Override
    public void write(Chunk<? extends Transaction> chunk) {
        transactionRepository.saveAll(chunk.getItems());
    }
}