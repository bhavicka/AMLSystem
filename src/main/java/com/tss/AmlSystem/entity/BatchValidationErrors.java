package com.tss.AmlSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "batch_validation_errors")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchValidationErrors extends BaseEntity{

    private TransactionBatch transactionBatch;
    private Integer rowNumber;
    private String fieldName;
    private String errorMessage;
}
