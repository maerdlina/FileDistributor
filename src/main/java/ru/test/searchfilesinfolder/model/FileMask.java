package ru.test.searchfilesinfolder.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class FileMask {
    private String pattern;
    private String targetDb;
    private Boolean isActive;
    private Date endDate;
    private String fileFormat;
    private int mask_id;
}
