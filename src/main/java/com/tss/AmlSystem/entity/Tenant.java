package com.tss.AmlSystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Table(name = "tenants", schema = "public")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tenant extends BaseEntity{
    @Column(unique = true, nullable = false)
    private String bankName;
    @Column(unique = true, nullable = false)
    private String ifsc;
    @Column(unique = true, nullable = false)
    private String contactEmail;
    @Column(unique = true, nullable = false)
    private String schemaName;
    @ColumnDefault("TRUE")
    @Column(nullable = false)
    private Boolean isActive = true;
    private LocalDateTime deactivatedAt;
}
