package ru.test.searchfilesinfolder.service;

import org.springframework.stereotype.Service;
import ru.test.searchfilesinfolder.model.FileMask;
import ru.test.searchfilesinfolder.repository.FileMaskRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FileMaskService {
    private final FileMaskRepository fileMaskRepository;

    public FileMaskService(FileMaskRepository fileMaskRepository) {
        this.fileMaskRepository = fileMaskRepository;
    }

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
}
