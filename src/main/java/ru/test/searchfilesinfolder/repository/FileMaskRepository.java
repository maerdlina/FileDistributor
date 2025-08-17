package ru.test.searchfilesinfolder.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.test.searchfilesinfolder.model.FileMetadata;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

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
}