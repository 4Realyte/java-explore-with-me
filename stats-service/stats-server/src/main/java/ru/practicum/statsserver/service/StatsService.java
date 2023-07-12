package ru.practicum.statsserver.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsserver.dao.StatsRepository;
import ru.practicum.statsserver.mapper.StatsMapper;
import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.statsserver.model.QStatsModel.statsModel;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository repository;
    private final StatsMapper mapper;

    public void addHit(EndpointHit dto) {
        repository.save(mapper.dtoToModel(dto));
    }

    public List<ViewStats> getAllStats(GetRequestStats request) {
        List<Predicate> predicates = new ArrayList<>();
        if (!request.getUri().isEmpty()) {
            predicates.add(statsModel.app.in(request.getUri()));
        }
        predicates.add(statsModel.timestamp.between(request.getStart(), request.getEnd()));
        if (request.getUnique()) {

        }
        return mapper.toViewStats(repository.findAll(ExpressionUtils.allOf(predicates)));
    }
}
