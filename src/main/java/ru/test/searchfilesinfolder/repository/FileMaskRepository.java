package ru.test.searchfilesinfolder.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileMaskRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileMaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getActiveCsvPatterns() {
        String sql = "SELECT PATTERN FROM FILE_MASK "
                + "WHERE FILEFORMAT = 'CSV' "
                + "AND ISACTIVE = true "
                + "AND (ENDDATE IS NULL OR ENDDATE >= CURRENT_DATE)";

        return jdbcTemplate.queryForList(sql, String.class);
    }
}