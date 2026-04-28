package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.response.CaseDashboardDto;
import com.tss.AmlSystem.dto.response.CaseDetailDto;
import com.tss.AmlSystem.entity.tenant.Case;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CaseMapper {

    @Mapping(target = "assignedTo", source = "assignedTo.email")
    CaseDashboardDto toResponseDto(Case caseEntity);

    List<CaseDashboardDto> toResponseDtoList(List<Case> caseEntities);

    @Mapping(source = "caseReferenceNumber", target = "caseReferenceNumber")
    @Mapping(source = "status", target = "caseStatus")
    @Mapping(source = "assignedTo.email", target = "assignedTo")
    @Mapping(source = "assignedBy.email", target = "assignedBy")
    CaseDetailDto toDetailResponseDto(Case caseEntity);

}
