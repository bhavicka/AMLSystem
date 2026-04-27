package com.tss.AmlSystem.dto.response;

public record FileUploadProcessDto (
        String message,
        String fileName,
        Integer totalRecords,
        String status
){
}
