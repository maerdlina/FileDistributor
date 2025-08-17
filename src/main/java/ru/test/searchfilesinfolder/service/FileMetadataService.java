package ru.test.searchfilesinfolder.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import ru.test.searchfilesinfolder.model.FileMask;
import ru.test.searchfilesinfolder.model.FileMetadata;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;

@Service
public class FileMetadataService {
    private FileMaskService fileMaskService;

    public FileMetadataService(FileMaskService fileMaskService) {
        this.fileMaskService = fileMaskService;
    }

    public List<FileMetadata> createFileMetadataList(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            return Collections.emptyList();
        }

        List<FileMask> activeMasks = fileMaskService.getActiveFileMask();
        if (activeMasks.isEmpty()) {
            return Collections.emptyList();
        }

        File[] allFiles = dir.listFiles();
        if (allFiles == null) {
            return Collections.emptyList();
        }

        List<FileMetadata> correctFiles = new ArrayList<>();

        for (File file : allFiles) {

            String fileName = file.getName();
            String baseName = getBaseName(fileName);
            String extension = getFileExtension(fileName).toLowerCase();

            for (FileMask mask : activeMasks) {
                String maskExtension = mask.getFileFormat().toLowerCase();

                // Проверяем только соответствие расширения и паттерна для основной части имени
                if (maskExtension.equals(extension) &&
                        fileMaskService.matchesPattern(baseName, mask.getPattern())) {

                    FileMetadata correctFile = new FileMetadata();
                    correctFile.setFileName(file.getName());
                    correctFile.setFileSize((int) FileUtils.sizeOf(file));
                    correctFile.setFileLastModified(new Timestamp(file.lastModified()));
                    correctFile.setProcessStartTime(null);
                    correctFile.setProcessEndTime(null);
                    correctFile.setStatus("processing");
                    correctFile.setMask_id(mask.getMask_id());
                    correctFiles.add(correctFile);
                    break;

                }
            }
        }

        return correctFiles;
    }

    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
