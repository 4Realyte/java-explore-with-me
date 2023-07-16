package ru.practicum.statsserver.dao;

import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.util.List;

public interface StatsRepository {
    void addHit(EndpointHit hit);

    List<ViewStats> getAllStats(GetRequestStats request);
}
