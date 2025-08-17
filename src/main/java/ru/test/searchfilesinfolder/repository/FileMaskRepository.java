package ru.test.searchfilesinfolder.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.test.searchfilesinfolder.model.FileCorrect;

import java.io.File;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FileMaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public FileMaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getFileMaskData(){
        String sql = "SELECT * FROM FILE_MASK "
                + "WHERE FILEFORMAT = 'CSV' "
                + "AND ISACTIVE = true "
                + "AND (ENDDATE IS NULL OR ENDDATE >= CURRENT_DATE)";

        return jdbcTemplate.queryForList(sql);
    }

    // UPDATE
    @Transactional
    public boolean upsertMetadata(List<FileCorrect> files) {
        if (files.isEmpty()) return false;

        String upsertSql = "MERGE INTO metadata AS target " +
                "USING (VALUES (?, ?, ?, ?, ?, ?, ?, ?)) " +
                "AS source (file_name, file_size, file_last_modified, process_start_time, " +
                "process_end_time, status, error_text, mask_id) " +
                "ON target.file_name = source.file_name " +
                "WHEN MATCHED AND ( " +
                "target.file_size != source.file_size OR " +
                "target.file_last_modified != source.file_last_modified " +
                ") THEN UPDATE SET " +
                "target.file_size = source.file_size, " +
                "target.file_last_modified = source.file_last_modified, " +
                "target.process_start_time = source.process_start_time, " +
                "target.process_end_time = NULL, " +
                "target.status = 'UPDATED', " +
                "target.error_text = NULL, " +
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
                FileCorrect file = files.get(i);
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

//    public Set<String> getExistingFileChecksums(List<FileCorrect> files) {
//        if (files.isEmpty()) return Collections.emptySet();
//
//        // Создаем список параметров для IN-условия
//        List<Object[]> params = new ArrayList<>();
//        for (FileCorrect file : files) {
//            params.add(new Object[]{
//                    file.getFileName(),
//                    file.getFileSize(),
//                    file.getFileLastModified()
//            });
//        }
//
//        // Запрос для поиска совпадений по трем ключевым полям
//        String sql = "SELECT CONCAT(file_name, '|', file_size, '|', file_last_modified) AS checksum "
//                + "FROM metadata "
//                + "WHERE (file_name, file_size, file_last_modified) IN ("
//                + String.join(",", Collections.nCopies(params.size(), "(?,?,?)"))
//                + ")";
//
//        return new HashSet<>(
//                jdbcTemplate.queryForList(
//                        sql,
//                        params.stream().flatMap(Arrays::stream).toArray(),
//                        String.class
//                )
//        );
//    }

}