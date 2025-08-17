package ru.test.searchfilesinfolder.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import ru.test.searchfilesinfolder.model.FileCorrect;
import ru.test.searchfilesinfolder.model.FileMask;
import ru.test.searchfilesinfolder.repository.FileMaskRepository;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class WatcherService {
    private final FileMaskRepository fileMaskRepository;
    private final FileMaskService fileMaskService;

    public WatcherService(FileMaskRepository fileMaskRepository, FileMaskService fileMaskService) {
        this.fileMaskRepository = fileMaskRepository;
        this.fileMaskService = fileMaskService;
    }

    // Send FilesCorrect to Repository
    public boolean sendMetadataToH2(String dirPath){
        List<FileCorrect> files = createFileCorrectList(dirPath);
        return fileMaskRepository.upsertMetadata(files);
    }

    // Active FileMask in List<FileMask>
    public List<FileMask> getActiveFileMask(){
        // Список для активных масок
        List<FileMask> activeFileMask = new ArrayList<>();

        // Активные маски в виде Map<Integer, Object>
        List<Map<String, Object>> fileMasks = fileMaskRepository.getFileMaskData();

        // Сохранение масок в списке List<FileMask>
        for(Map<String, Object> fileMask: fileMasks){
            FileMask mask = new FileMask();
            mask.setMask_id((Integer) fileMask.get("mask_id"));
            mask.setPattern((String) fileMask.get("pattern"));
            mask.setTargetDb((String) fileMask.get("target_db"));
            mask.setIsActive((Boolean) fileMask.get("isactive"));
            mask.setEndDate((Date) fileMask.get("enddate"));
            mask.setFileFormat((String) fileMask.get("fileformat"));
            activeFileMask.add(mask);
        }

        return activeFileMask;
    }

    // Correct files in List<FileCorrect>
    public List<FileCorrect> createFileCorrectList(String dirPath) {
        File dir = new File(dirPath);

        Collection<File> files = FileUtils.listFiles(dir, new String[]{"csv"}, false);

        if (files == null) {
            return new ArrayList<>();
        }

        List<FileMask> activeMasks = fileMaskService.getActiveFileMask();
        List<FileCorrect> correctFiles = new ArrayList<>();

        for (File file : files) {
            for (FileMask mask : activeMasks) {
                if (matchesPattern(file.getName(), mask.getPattern())) {
                    FileCorrect correctFile = new FileCorrect();
                    correctFile.setFileName(file.getName());
                    correctFile.setFileSize((int) FileUtils.sizeOf(file));
                    correctFile.setFileLastModified(new Timestamp(file.lastModified()));
                    correctFile.setProcessStartTime(Timestamp.from(Instant.now()));
                    correctFile.setProcessEndTime(Timestamp.from(Instant.now()));
                    correctFile.setStatus("processing");
                    correctFile.setMask_id(mask.getMask_id());
                    correctFiles.add(correctFile);
                    break; // Прерываем цикл после первого совпадения
                }
            }
        }

        return correctFiles;
    }

    private boolean matchesPattern(String fileName, String pattern) {
        try {
            // Создаем PathMatcher для glob-шаблона
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            return matcher.matches(java.nio.file.Paths.get(fileName));
        } catch (Exception e) {
            System.err.println("Ошибка в шаблоне '" + pattern + "': " + e.getMessage());
            return false;
        }
    }

}
