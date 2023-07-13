package ru.practicum.statsserver.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addHit(EndpointHit hit) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("stats")
                .usingGeneratedKeyColumns("id");

        simpleJdbcInsert.execute(Map.of("app", hit.getApp(),
                "uri", hit.getUri(),
                "ip", hit.getIp(),
                "creation_date", LocalDateTime.parse(hit.getTimestamp(),formatter)));

    }

    public List<ViewStats> getAllStats(GetRequestStats request) {
        String unique = "COUNT(s.ip)";
        if (request.getUnique()) {
            unique = "COUNT(DISTINCT s.ip)";
        }

        String sql = "SELECT s.app, s.uri, " + unique + " as hits FROM stats AS s " +
                "WHERE s.creation_date BETWEEN ? AND ? ";
        StringBuilder builder = new StringBuilder(sql);

        List<String> uri = request.getUris();
        if (!uri.isEmpty()) {
            String sqlParam = String.join(",", Collections.nCopies(uri.size(), "?"));
            builder.append("AND s.uri IN" + "(" + sqlParam + ")");
            builder.append(" GROUP BY s.app, s.uri");
            return jdbcTemplate.query(builder.toString(), (rs, rowNum) -> makeViewStat(rs),
                    request.getStart(), request.getEnd(), uri.toArray());
        }
        builder.append(" GROUP BY s.app, s.uri");
        return jdbcTemplate.query(builder.toString(), (rs, rowNum) -> makeViewStat(rs),
                request.getStart(), request.getEnd());
    }

    private ViewStats makeViewStat(ResultSet rs) throws SQLException {
        return ViewStats.builder()
                .app(rs.getString("app"))
                .uri(rs.getString("uri"))
                .hits(rs.getInt("hits"))
                .build();
    }
}
