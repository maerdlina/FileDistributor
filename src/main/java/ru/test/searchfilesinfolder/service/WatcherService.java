package ru.test.searchfilesinfolder.service;

import org.springframework.stereotype.Service;
import ru.test.searchfilesinfolder.model.FileMetadata;
import ru.test.searchfilesinfolder.repository.FileMaskRepository;
import ru.test.searchfilesinfolder.repository.FileMetadataRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class WatcherService {
    private final FileMaskRepository fileMaskRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final FileMaskService fileMaskService;
    private final FileMetadataService fileMetadataService;

    public WatcherService(FileMaskRepository fileMaskRepository, FileMaskService fileMaskService, FileMetadataRepository fileMetadataRepository, FileMetadataService fileMetadataService) {
        this.fileMaskRepository = fileMaskRepository;
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileMaskService = fileMaskService;
        this.fileMetadataService = fileMetadataService;
    }

    // Send FilesCorrect to Repository
    public boolean sendMetadataToH2(String dirPath) {
        List<FileMetadata> files = fileMetadataService.createFileMetadataList(dirPath);
        if (files.isEmpty()) {
            return false;
        }

        // Этап 1: Подготовка к обработке
        Timestamp startTime = Timestamp.from(Instant.now());
        for (FileMetadata file : files) {
            file.setProcessStartTime(startTime);
            file.setProcessEndTime(null); // Пока не установлено
            file.setStatus("processing");
        }
        fileMetadataRepository.upsertMetadata(files);

        // Этап 2: Завершение обработки
        Timestamp endTime = Timestamp.from(Instant.now());
        fileMetadataRepository.updateEndTimeAndStatus(files, endTime, "processed");

        return true;
    }


}
