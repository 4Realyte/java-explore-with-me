package ru.practicum.statsserver.mapper;

import org.mapstruct.Mapper;
import ru.practicum.statsserver.model.StatsModel;
import stats.EndpointHit;
import stats.ViewStats;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    StatsModel dtoToModel(EndpointHit dto);

    ViewStats toViewStats(StatsModel model);

    List<ViewStats> toViewStats(Iterable<StatsModel> models);
}
