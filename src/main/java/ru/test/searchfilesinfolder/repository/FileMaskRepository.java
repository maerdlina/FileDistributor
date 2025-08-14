package ru.test.searchfilesinfolder.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class FileMaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public FileMaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get Pattern
    public List<String> getActiveCsvPatterns() {
        String sql = "SELECT PATTERN FROM FILE_MASK "
                + "WHERE FILEFORMAT = 'CSV' "
                + "AND ISACTIVE = true "
                + "AND (ENDDATE IS NULL OR ENDDATE >= CURRENT_DATE)";

        return jdbcTemplate.queryForList(sql, String.class);
    }

    // Send metadata to H2
    @Transactional
    public boolean sendMetadataToDB(List<File> correctFiles) {
        if (correctFiles.isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO metadata"
                + " (file_name, file_size, process_start_time, process_end_time, status, error_text, mask_id)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?)"; // Убрали METADATA_ID

        Timestamp now = Timestamp.from(Instant.now());

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                File correctFile = correctFiles.get(i);
                ps.setString(1, correctFile.getName());
                ps.setLong(2, correctFile.length());
                ps.setTimestamp(3, now);
                ps.setTimestamp(4, now);
                ps.setString(5, "processing");
                ps.setString(6, "success");
                ps.setLong(7, 1L); // Исправлен тип данных для mask_id
            }

            @Override
            public int getBatchSize() {
                return correctFiles.size();
            }
        });

        return true; // Убрали лишний update
    }
}