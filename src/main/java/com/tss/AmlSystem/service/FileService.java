package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.FileDetailDto;
import com.tss.AmlSystem.dto.response.FileErrorInlineDto;
import com.tss.AmlSystem.dto.response.FileInlineDto;
import com.tss.AmlSystem.entity.enums.LogTag;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.mapper.FileMapper;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.FileValidationErrorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileValidationErrorsRepository errorsRepository;
    private final FileMapper fileMapper;

    public Slice<FileInlineDto> getAllFiles(Pageable pageable){
        log.info("{} Fetching all files with pagination: page number {}, page size {}", LogTag.BATCH.getValue(), pageable.getPageNumber(), pageable.getPageSize());
        Slice<File> files = fileRepository.findAllOrderByCreatedAtDesc(pageable);
        return files.map(fileMapper::toFileInlineDto);
    }

    public FileDetailDto getFileDetails(String fileNumber){
        log.info("{} Fetching file details for file number: {}", LogTag.BATCH.getValue(), fileNumber);
        File file = fileRepository.findByFileNumber(UUID.fromString(fileNumber)).orElseThrow(
                () -> new RuntimeException("File not found with file number: " + fileNumber)
        );
        return fileMapper.toFileDetailDto(file);
    }

    public Slice<FileErrorInlineDto> getErrorsForFile(String fileNumber, Pageable pageable) {
        log.info("{} Fetching file validation errors for file number: {} with pagination: page number {}, page size {}", LogTag.BATCH.getValue(), fileNumber, pageable.getPageNumber(), pageable.getPageSize());
        File file = fileRepository.findByFileNumber(UUID.fromString(fileNumber)).orElseThrow(
                () -> new RuntimeException("File not found with file number: " + fileNumber)
        );
        Slice<FileValidationErrors> errorsSlice = errorsRepository.findByFileId(file.getId(), pageable);
        return errorsSlice.map(error -> new FileErrorInlineDto(
                error.getRowNumber(),
                error.getFieldName(),
                error.getErrorMessage()
        ));
    }
}
