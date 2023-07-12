package ru.practicum.statsserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsserver.service.StatsService;
import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    public void makeHit(@RequestBody EndpointHit requestDto) {
        service.addHit(requestDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getAllStats(@RequestParam LocalDateTime start,
                                       @RequestParam LocalDateTime end,
                                       @RequestParam(required = false) List<String> uri,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getAllStats(GetRequestStats.of(start, end, uri, unique));
    }
}
