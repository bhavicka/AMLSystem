package com.tss.AmlSystem.entity.tenant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "str_filling")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StrFilling extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private Case aCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filed_by", nullable = false)
    private TenantUser filedBy;

    @Column(name = "supporting_notes")
    private String supportingNotes;

    @Column(unique = true, nullable = false, name = "reference_number")
    private String referenceNumber;

    @Column(unique = true, nullable = false, name = "pdf_storage_path")
    private String pdfStoragePath;
}
