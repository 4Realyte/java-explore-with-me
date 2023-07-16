package ru.practicum.statsserver.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class StatsRepositoryImpl implements StatsRepository {
    private final NamedParameterJdbcTemplate namedJdbc;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public StatsRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbc) {
        this.namedJdbc = namedJdbc;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("stats")
                .usingGeneratedKeyColumns("id");
    }

    public void addHit(EndpointHit hit) {
        jdbcInsert.execute(Map.of("app", hit.getApp(),
                "uri", hit.getUri(),
                "ip", hit.getIp(),
                "creation_date", hit.getTimestamp()));
    }

    public List<ViewStats> getAllStats(GetRequestStats request) {
        MapSqlParameterSource mapParam = new MapSqlParameterSource(Map.of("start", request.getStart(),
                "end", request.getEnd(),
                "uris", request.getUris()));
        String unique = "COUNT(s.ip)";
        if (request.getUnique()) {
            unique = "COUNT(DISTINCT s.ip)";
        }

        String sql = "SELECT s.app, s.uri, " + unique + " as hits FROM stats AS s " +
                "WHERE s.creation_date BETWEEN :start AND :end ";
        StringBuilder builder = new StringBuilder(sql);

        List<String> uri = request.getUris();
        if (!uri.isEmpty()) {
            builder.append("AND s.uri IN (:uris)");
        }
        builder.append(" GROUP BY s.app, s.uri ORDER BY hits DESC");
        return namedJdbc.query(builder.toString(), mapParam, (rs, rowNum) -> makeViewStat(rs));
    }

    private ViewStats makeViewStat(ResultSet rs) throws SQLException {
        return ViewStats.builder()
                .app(rs.getString("app"))
                .uri(rs.getString("uri"))
                .hits(rs.getInt("hits"))
                .build();
    }
}
