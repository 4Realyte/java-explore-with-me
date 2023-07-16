package ru.practicum.statsserver.service;

import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.util.List;

public interface StatsService {
    void addHit(EndpointHit dto);

    List<ViewStats> getAllStats(GetRequestStats request);
}
