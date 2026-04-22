package com.tss.AmlSystem.batch.writer;

import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerItemWriter implements ItemWriter<Customer> {

    private final CustomerRepository customerRepository;

    @Override
    public void write(Chunk<? extends Customer> chunk) {
        // chunk.getItems() is a List<Customer> of size=chunkSize (1000)
        // saveAll does a single batch INSERT — very fast
        customerRepository.saveAll(chunk.getItems());
    }
}
