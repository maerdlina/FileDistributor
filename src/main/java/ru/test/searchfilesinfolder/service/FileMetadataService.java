package ru.test.searchfilesinfolder.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import ru.test.searchfilesinfolder.model.FileMask;
import ru.test.searchfilesinfolder.model.FileMetadata;

import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FileMetadataService {
    private FileMaskService fileMaskService;

    public FileMetadataService(FileMaskService fileMaskService) {
        this.fileMaskService = fileMaskService;
    }

    public List<FileMetadata> createFileMetadataList(String dirPath) {
        File dir = new File(dirPath);

        Collection<File> files = FileUtils.listFiles(dir, new String[]{"csv"}, false);

        if (files == null) {
            return new ArrayList<>();
        }

        List<FileMask> activeMasks = fileMaskService.getActiveFileMask();
        List<FileMetadata> correctFiles = new ArrayList<>();

        for (File file : files) {
            for (FileMask mask : activeMasks) {
                if (fileMaskService.matchesPattern(file.getName(), mask.getPattern())) {
                    FileMetadata correctFile = new FileMetadata();
                    correctFile.setFileName(file.getName());
                    correctFile.setFileSize((int) FileUtils.sizeOf(file));
                    correctFile.setFileLastModified(new Timestamp(file.lastModified()));
                    correctFile.setProcessStartTime(null);
                    correctFile.setProcessEndTime(null);
                    correctFile.setStatus("processing");
                    correctFile.setMask_id(mask.getMask_id());
                    correctFiles.add(correctFile);
                    break; // Прерываем цикл после первого совпадения
                }
            }
        }

        return correctFiles;
    }
}
