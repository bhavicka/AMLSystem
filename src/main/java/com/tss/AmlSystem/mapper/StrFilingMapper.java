package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.response.StrFilingDetailDto;
import com.tss.AmlSystem.entity.tenant.StrFilling;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StrFilingMapper {

    @Mapping(target = "caseReferenceNumber", source = "strFilling.referenceNumber")
    @Mapping(target = "filedBy", expression = "java(strFilling.getFiledBy().getFirstName() + ' ' + strFilling.getFiledBy().getLastName())")
    @Mapping(target = "referenceNumber", source = "strFilling.referenceNumber")
    @Mapping(target = "filedAt", source = "strFilling.createdAt")
    @Mapping(target = "pdfStoragePath", source = "strFilling.pdfStoragePath")
    @Mapping(target = "clientNumber", source = "clientNumber")
    StrFilingDetailDto toStrFilingDetailDto(StrFilling strFilling, String clientNumber);
}
