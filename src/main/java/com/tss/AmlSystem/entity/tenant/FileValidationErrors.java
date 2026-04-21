package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
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

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;
}
