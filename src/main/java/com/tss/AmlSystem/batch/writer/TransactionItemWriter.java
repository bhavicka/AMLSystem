package com.tss.AmlSystem.batch.writer;

import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.repository.AccountRepository;
import com.tss.AmlSystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.tss.AmlSystem.entity.enums.LogTag;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionItemWriter implements ItemWriter<Transaction> {

    private final TransactionRepository transactionRepository;

    @Override
    public void write(Chunk<? extends Transaction> chunk) {
        log.debug("{} Writing chunk of size: {}", LogTag.BATCH.getValue(), chunk.size());
        try {
            transactionRepository.saveAll(chunk.getItems());
            log.debug("{} Successfully saved chunk", LogTag.BATCH.getValue());
        } catch (Exception e) {
            log.error("{} Writer error: {}", LogTag.BATCH.getValue(), e.getMessage());
            throw e;
        }
    }
}