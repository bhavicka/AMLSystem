package com.tss.AmlSystem.batch.writer;

import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountItemWriter implements ItemWriter<Account> {

    private final AccountRepository accountRepository;

    @Override
    public void write(Chunk<? extends Account> chunk) {
        System.out.println("DEBUG: Writing chunk of size: " + chunk.size());
        try {
            accountRepository.saveAll(chunk.getItems());
            System.out.println("DEBUG: Successfully saved chunk");
        } catch (Exception e) {
            System.err.println("DEBUG: Writer error: " + e.getMessage());
            throw e;
        }
    }
}