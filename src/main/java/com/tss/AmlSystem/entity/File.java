package com.tss.AmlSystem.entity;

import com.tss.AmlSystem.entity.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Table(name = "files")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class File extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
    @Column(nullable = false)
    private String fileName;
    private String fileStoragePath;
    @Column(nullable = false)
    private Long fileSizeBytes;
    @Column(nullable = false)
    private Integer totalRecords;
    @Column(nullable = false)
    @ColumnDefault("uploaded")
    @Enumerated(EnumType.STRING)
    private BatchStatus status = BatchStatus.UPLOADED;
    private LocalDateTime processedAt;
    @Column(nullable = false)
    @ColumnDefault("FALSE")
    private Boolean isRemoved = false;
}
