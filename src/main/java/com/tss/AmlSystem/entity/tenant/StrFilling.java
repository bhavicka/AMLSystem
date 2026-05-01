package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private Case aCase;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filed_by", nullable = false)
    private TenantUser filedBy;

    @Column(name = "supporting_notes")
    private String supportingNotes;

    @NotBlank
    @Column(unique = true, nullable = false, name = "reference_number")
    private String referenceNumber;

    @NotBlank
    @Size(max = 500)
    @Column(unique = true, nullable = false, name = "pdf_storage_path")
    private String pdfStoragePath;
}
