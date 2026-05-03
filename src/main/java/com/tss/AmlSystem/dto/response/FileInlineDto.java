package com.tss.AmlSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInlineDto {
    private String fileNumber;
    private String fileName;
    private String fileType;
    private LocalDate uploadDate;
}
