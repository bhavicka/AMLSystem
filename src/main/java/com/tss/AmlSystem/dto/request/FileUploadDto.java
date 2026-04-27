package com.tss.AmlSystem.dto.request;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadDto {
    private FileType fileType;
}
