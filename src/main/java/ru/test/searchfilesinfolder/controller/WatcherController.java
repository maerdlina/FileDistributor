package ru.test.searchfilesinfolder.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.searchfilesinfolder.service.WatcherService;

import java.io.File;

@RestController()
@RequestMapping("/api/files")
public class WatcherController {

    private final WatcherService watcherService;
//    private final String scanPath = "C:/Users/maerd/Documents/Search/";
    @Value("${search.scan-path}") // Инъекция значения из конфига
    private String scanPath;

    public WatcherController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @GetMapping("/checkCsv&Mask")
    public ResponseEntity<File[]> listCsvFiles() {
        return ResponseEntity.ok(watcherService.getFileList(scanPath));
    }

    @PostMapping("/sendMetadataToH2")
    public ResponseEntity<Boolean> sendMetadataToDB() {
        return ResponseEntity.ok(watcherService.sendMetadataToDB(scanPath));
    }
}