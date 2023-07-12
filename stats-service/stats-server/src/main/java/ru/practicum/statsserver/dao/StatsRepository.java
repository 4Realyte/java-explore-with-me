package ru.practicum.statsserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.statsserver.model.StatsModel;
import stats.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatsModel, Long>, QuerydslPredicateExecutor<StatsModel> {
    @Query(value = "SELECT new stats.ViewStats(s.app, s.uri, count(s.id)) " +
            "FROM StatsModel as s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "group by s.id")
    List<ViewStats> findAllStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
