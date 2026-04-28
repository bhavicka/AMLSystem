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

@Table(name = "file_validation_errors")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileValidationErrors extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Size(max = 255)
    @Column(name = "field_name")
    private String fieldName;

    @NotBlank
    @Size(max = 500)
    @Column(name = "error_message", nullable = false)
    private String errorMessage;
}
