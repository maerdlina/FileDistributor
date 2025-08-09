package ru.test.searchfilesinfolder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.searchfilesinfolder.service.WatcherService;

import java.io.File;

@RestController()
@RequestMapping("/api/files")
public class WatcherController {

    private final WatcherService watcherService;
    private final String scanPath = "C:/Users/maerd/Documents/Search/"; // Лучше вынести в @Value

    public WatcherController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @GetMapping("/csv")
    public ResponseEntity<File[]> listCsvFiles() {
        return ResponseEntity.ok(watcherService.getFileList(scanPath));
    }
}