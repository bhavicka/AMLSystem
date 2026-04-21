package com.tss.AmlSystem.entity.tenant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "batches")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Batch extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private TenantUser uploadedBy;
}
