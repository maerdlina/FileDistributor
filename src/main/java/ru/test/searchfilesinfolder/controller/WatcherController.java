package ru.test.searchfilesinfolder.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.searchfilesinfolder.model.FileCorrect;
import ru.test.searchfilesinfolder.model.FileMask;
import ru.test.searchfilesinfolder.service.FileMaskService;
import ru.test.searchfilesinfolder.service.WatcherService;

import java.util.List;

@RestController()
@RequestMapping("/api/files")
public class WatcherController {

    private final WatcherService watcherService;

    @Value("${search.scan-path}")
    private String scanPath;

    public WatcherController(WatcherService watcherService, FileMaskService fileMaskService) {
        this.watcherService = watcherService;
    }

    @GetMapping("/getActiveFileMask")
    public ResponseEntity<List<FileMask>> getActiveFileMask(){
        return ResponseEntity.ok(watcherService.getActiveFileMask());
    }

    @GetMapping("/getFileCorrectList")
    public ResponseEntity<List<FileCorrect>> getFileCorrectList(){
        return ResponseEntity.ok(watcherService.createFileCorrectList(scanPath));
    }

    @PostMapping("/sendMetadataToH2")
    public ResponseEntity<Boolean> sendMetadataToDB() {
        return ResponseEntity.ok(watcherService.sendMetadataToH2(scanPath));
    }
}