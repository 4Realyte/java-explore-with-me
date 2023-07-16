package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsserver.dao.StatsRepository;
import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Transactional
    public void addHit(EndpointHit dto) {
        repository.addHit(dto);
    }

    public List<ViewStats> getAllStats(GetRequestStats request) {
        return repository.getAllStats(request);
    }
}
