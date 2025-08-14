package ru.test.searchfilesinfolder.service;

import org.springframework.stereotype.Service;
import ru.test.searchfilesinfolder.repository.FileMaskRepository;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

@Service
public class WatcherService {
    private final FileMaskRepository fileMaskRepository;

    public WatcherService(FileMaskRepository fileMaskRepository) {
        this.fileMaskRepository = fileMaskRepository;
    }

    // Correct file or not - Get
    public File[] getFileList(String dirPath) {
        File dir = new File(dirPath);
        File[] fileList = dir.listFiles((dir1, name) ->
                name.toLowerCase().endsWith(".csv") && hasValidStructure(name)
        );

        if(fileList == null || fileList.length == 0)
            return new File[0];

        List<String> patterns = fileMaskRepository.getActiveCsvPatterns();
        if (patterns.isEmpty()) return fileList;

        List<File> matchedFiles = new ArrayList<>();
        for(File file : fileList) {
            if(matchesAnyPattern(file.getName(), patterns))
                matchedFiles.add(file);
        }

        return fileList;
    }

    // Sent Metadata to H2
    public boolean sendMetadataToDB(String path){
        List<File> correctFiles = List.of(getFileList(path));
        return fileMaskRepository.sendMetadataToDB(correctFiles);
    }

    private boolean hasValidStructure(String fileName) {
        // Не учитываем регистр для расширения
        if (!fileName.toLowerCase().endsWith(".csv")) {
            return false;
        }

        // Удаляем расширение
        String baseName = fileName.substring(0, fileName.length() - 4);
        String[] parts = baseName.split("_");

        if (parts.length != 3) {
            return false;
        }

        // Проверяем префикс
        String prefix = parts[0].toUpperCase();
        if (!"SVD".equals(prefix) && !"CDD".equals(prefix)) {
            return false;
        }

        // Проверяем тип
        String type = parts[2].toLowerCase();
        if (!"report".equals(type) && !"info".equals(type)) {
            return false;
        }

        return parts[1].matches("\\d{8}");
    }

    private boolean matchesAnyPattern(String fileName, List<String> patterns) {
        for(String pattern : patterns) {
            try{
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:"
                + pattern);

                if(matcher.matches(java.nio.file.Paths.get(fileName)))
                    return true;
            } catch(Exception e){
                System.err.println("Ошибка в шаблоне '" + pattern + "': " + e.getMessage());
            }
        }
        return false;
    }
}