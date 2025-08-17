package ru.test.searchfilesinfolder.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.searchfilesinfolder.model.FileMetadata;
import ru.test.searchfilesinfolder.model.FileMask;
import ru.test.searchfilesinfolder.service.FileMaskService;
import ru.test.searchfilesinfolder.service.FileMetadataService;
import ru.test.searchfilesinfolder.service.WatcherService;

import java.util.List;

@RestController()
@RequestMapping("/api/files")
public class WatcherController {

    private final WatcherService watcherService;
    private final FileMaskService fileMaskService;
    private final FileMetadataService fileMetadataService;

    @Value("${search.scan-path}")
    private String scanPath;

    public WatcherController(WatcherService watcherService, FileMetadataService fileMetadataService, FileMaskService fileMaskService) {
        this.watcherService = watcherService;
        this.fileMaskService = fileMaskService;
        this.fileMetadataService = fileMetadataService;
    }

    @GetMapping("/getActiveFileMask")
    public ResponseEntity<List<FileMask>> getActiveFileMask(){
        return ResponseEntity.ok(fileMaskService.getActiveFileMask());
    }

    @GetMapping("/getFileCorrectList")
    public ResponseEntity<List<FileMetadata>> getFileCorrectList(){
        return ResponseEntity.ok(fileMetadataService.createFileMetadataList(scanPath));
    }

    @PostMapping("/sendMetadataToH2")
    public ResponseEntity<Boolean> sendMetadataToDB() {
        return ResponseEntity.ok(watcherService.sendMetadataToH2(scanPath));
    }
}