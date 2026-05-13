package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.response.CaseDashboardDto;
import com.tss.AmlSystem.dto.response.CaseDetailDto;
import com.tss.AmlSystem.entity.tenant.Case;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CaseMapper {

    default String mapUserToName(TenantUser user) {
        if (user == null) return null;
        return user.getFirstName() + " " + user.getLastName();
    }

    @Mapping(target = "assignedTo", expression = "java(mapUserToName(caseEntity.getAssignedTo()))")
    @Mapping(target = "caseStatus",source = "status")
    CaseDashboardDto toResponseDto(Case caseEntity);

    List<CaseDashboardDto> toResponseDtoList(List<Case> caseEntities);

    @Mapping(source = "caseReferenceNumber", target = "caseReferenceNumber")
    @Mapping(source = "status", target = "caseStatus")
    @Mapping(target = "assignedTo", expression = "java(mapUserToName(caseEntity.getAssignedTo()))")
    @Mapping(target = "assignedBy", expression = "java(mapUserToName(caseEntity.getAssignedBy()))")
    CaseDetailDto toDetailResponseDto(Case caseEntity);



}
