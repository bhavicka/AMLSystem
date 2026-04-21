package com.tss.AmlSystem.entity.tenant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "file_validation_errors")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileValidationErrors extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;
    private Integer rowNumber;
    private String fieldName;
    private String errorMessage;
}
