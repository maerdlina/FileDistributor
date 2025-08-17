package ru.test.searchfilesinfolder.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class FileMetadata {
//    private Integer metadataId;
    private String fileName;
    private Integer fileSize;
    private Timestamp fileLastModified;
    private Timestamp processStartTime;
    private Timestamp processEndTime;
    private String status;
    private String error_text;
    private Integer mask_id;
}
