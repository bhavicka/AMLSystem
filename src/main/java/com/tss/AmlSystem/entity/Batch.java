package com.tss.AmlSystem.entity;

import com.tss.AmlSystem.entity.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Table(name = "batches")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Batch extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}
