package com.tss.AmlSystem.entity;

import com.tss.AmlSystem.entity.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Table(name = "transaction_batches")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionBatch extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
    @Column(nullable = false)
    private String fileName;
    private String fileStoragePath;
    @Column(nullable = false)
    private Integer totalRecords;
    @Column(nullable = false)
    @ColumnDefault("uploaded")
    @Enumerated(EnumType.STRING)
    private BatchStatus status = BatchStatus.UPLOADED;
    private LocalDateTime processedAt;
}
