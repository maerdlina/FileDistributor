package ru.test.searchfilesinfolder.service;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class WatcherService {

    public static File[] getFileList(String dirPath) {
        File dir = new File(dirPath);

        File[] fileList = dir.listFiles((dir1, name) -> name.endsWith(".csv"));
        return fileList;
    }
}