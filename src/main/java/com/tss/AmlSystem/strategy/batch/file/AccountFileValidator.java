package com.tss.AmlSystem.strategy.batch.file;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.FileValidationErrorsRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountFileValidator extends FileValidator{
    public AccountFileValidator(FileRepository fileRepository, FileValidationErrorsRepository fileValidationErrorsRepository) {
        super(fileRepository, fileValidationErrorsRepository);
    }

    @Override
    public FileType getFileType() {
        return FileType.ACCOUNTS;
    }
//
//    @Override
//    public List<FileValidationErrors> validate(Object dto, File file, int rowNumber) {
//        return List.of();
//    }
}
