package com.tss.AmlSystem.dto.request;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadDto {
    @NotNull(message = "File type must be specified")
    private FileType fileType;
}
