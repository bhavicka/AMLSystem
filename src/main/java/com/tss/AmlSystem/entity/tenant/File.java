package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.FileStatus;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "files")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class File extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private TenantUser uploadedBy;

    @Column(nullable = false, name = "file_name")
    private String fileName;

    @Column(nullable = false, name = "file_number", unique = true, updatable = false, columnDefinition = "uuid")
    private UUID fileNumber = UUID.randomUUID();

    @Column(name = "file_storage_path")
    private String fileStoragePath;

    @Column(nullable = false, name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(nullable = false, name = "total_records")
    private Integer totalRecords;

    @ColumnDefault("uploaded")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, name = "status", columnDefinition = "file_status")
    private FileStatus status = FileStatus.UPLOADED;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, name = "file_type", columnDefinition = "file_type")
    private FileType fileType;

    @Column(nullable = false, name = "is_removed")
    @ColumnDefault("FALSE")
    private Boolean isRemoved = false;
}
