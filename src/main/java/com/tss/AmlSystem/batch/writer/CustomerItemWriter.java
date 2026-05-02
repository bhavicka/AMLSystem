package com.tss.AmlSystem.batch.writer;

import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.tss.AmlSystem.entity.enums.LogTag;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerItemWriter implements ItemWriter<Customer> {

    private final CustomerRepository customerRepository;

    @Override
    public void write(Chunk<? extends Customer> chunk) {
        log.debug("{} Writing chunk of size: {}", LogTag.BATCH.getValue(), chunk.size());
        try {
            customerRepository.saveAll(chunk.getItems());
            log.debug("{} Successfully saved chunk", LogTag.BATCH.getValue());
        } catch (Exception e) {
            log.error("{} Writer error: {}", LogTag.BATCH.getValue(), e.getMessage());
            throw e;
        }
    }
}
