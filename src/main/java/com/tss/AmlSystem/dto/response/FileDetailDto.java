package com.tss.AmlSystem.dto.response;
import com.tss.AmlSystem.entity.enums.tenant.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDetailDto {
    private String fileNumber;
    private String fileName;
    private String fileType;
    private LocalDate uploadDate;
    private FileStatus fileStatus;
    private Integer totalRecords;
    private Long fileSizeInBytes;
}
