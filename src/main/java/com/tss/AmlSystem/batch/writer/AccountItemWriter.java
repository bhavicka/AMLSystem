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
        accountRepository.saveAll(chunk.getItems());
    }
}