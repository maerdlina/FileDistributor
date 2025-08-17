package ru.test.searchfilesinfolder.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.test.searchfilesinfolder.model.FileMetadata;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class FileMetadataRepository {

    private final JdbcTemplate jdbcTemplate;

    public FileMetadataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // UPDATE end time and status
    @Transactional
    public void updateEndTimeAndStatus(List<FileMetadata> files, Timestamp endTime, String status) {
        String sql = "UPDATE metadata SET process_end_time = ?, status = ? WHERE file_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                FileMetadata file = files.get(i);
                ps.setTimestamp(1, endTime);
                ps.setString(2, status);
                ps.setString(3, file.getFileName());
            }

            @Override
            public int getBatchSize() {
                return files.size();
            }
        });
    }

    // UPDATE or CREATE row in DB
    @Transactional
    public boolean upsertMetadata(List<FileMetadata> files) {
        if (files.isEmpty()) return false;

        String upsertSql = "MERGE INTO metadata AS target " +
                "USING (VALUES (?, ?, ?, ?, ?, ?, ?, ?)) " +
                "AS source (file_name, file_size, file_last_modified, process_start_time, " +
                "process_end_time, status, error_text, mask_id) " +
                "ON target.file_name = source.file_name " +
                "WHEN MATCHED THEN UPDATE SET " + // Упрощено условие
                "target.file_size = source.file_size, " +
                "target.file_last_modified = source.file_last_modified, " +
                "target.process_start_time = source.process_start_time, " +
                "target.process_end_time = source.process_end_time, " +
                "target.status = source.status, " +
                "target.error_text = source.error_text, " +
                "target.mask_id = source.mask_id " +
                "WHEN NOT MATCHED THEN INSERT " +
                "(file_name, file_size, file_last_modified, process_start_time, " +
                "process_end_time, status, error_text, mask_id) " +
                "VALUES (source.file_name, source.file_size, source.file_last_modified, " +
                "source.process_start_time, source.process_end_time, " +
                "source.status, source.error_text, source.mask_id)";

        jdbcTemplate.batchUpdate(upsertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                FileMetadata file = files.get(i);
                ps.setString(1, file.getFileName());
                ps.setLong(2, file.getFileSize());
                ps.setTimestamp(3, file.getFileLastModified());
                ps.setTimestamp(4, file.getProcessStartTime());
                ps.setTimestamp(5, null); // process_end_time
                ps.setString(6, file.getStatus());
                ps.setString(7, file.getError_text());
                ps.setInt(8, file.getMask_id());
            }

            @Override
            public int getBatchSize() {
                return files.size();
            }
        });

        return true;
    }
}
