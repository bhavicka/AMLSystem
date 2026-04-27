package com.tss.AmlSystem.factory;

import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.strategy.batch.joblaunch.FileJobLauncher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileJobLauncherFactory {

    private final Map<FileType, FileJobLauncher> launcherMap;

    public FileJobLauncherFactory(List<FileJobLauncher> launchers) {
        this.launcherMap = launchers.stream()
                .collect(Collectors.toMap(FileJobLauncher::getFileType, l -> l));
    }

    public FileJobLauncher getLauncher(FileType fileType) {
        FileJobLauncher launcher = launcherMap.get(fileType);
        if (launcher == null) {
            throw new IllegalArgumentException("No launcher found for fileType: " + fileType);
        }
        return launcher;
    }
}
