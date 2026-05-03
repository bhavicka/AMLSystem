package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.response.FileDetailDto;
import com.tss.AmlSystem.dto.response.FileInlineDto;
import com.tss.AmlSystem.entity.enums.tenant.FileStatus;
import com.tss.AmlSystem.entity.tenant.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface FileMapper {
    @Mapping(target = "fileNumber", source = "fileNumber")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "uploadDate", source = "createdAt")
    FileInlineDto toFileInlineDto(File fie);

    @Mapping(target = "fileNumber", source = "fileNumber")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "uploadDate", source = "createdAt")
    @Mapping(target = "fileStatus", source = "status")
    @Mapping(target = "totalRecords", source = "totalRecords")
    @Mapping(target = "fileSizeInBytes", source = "fileSizeBytes")
    FileDetailDto toFileDetailDto(File file);
}
