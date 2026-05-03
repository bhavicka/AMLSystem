package com.tss.AmlSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileErrorInlineDto {
    private Integer rowNumber;
    private String fieldName;
    private String errorMessage;
}
