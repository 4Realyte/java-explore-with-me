package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statsserver.dao.StatsRepositoryImpl;
import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepositoryImpl repository;

    public void addHit(EndpointHit dto) {
        repository.addHit(dto);
    }

    public List<ViewStats> getAllStats(GetRequestStats request) {
        return repository.getAllStats(request);
    }
}
