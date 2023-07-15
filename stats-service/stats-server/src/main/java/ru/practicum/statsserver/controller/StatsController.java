package ru.practicum.statsserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsserver.exception.IncorrectDateException;
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
    @ResponseStatus(HttpStatus.CREATED)
    public void makeHit(@RequestBody EndpointHit requestDto) {
        service.addHit(requestDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getAllStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IncorrectDateException("Дата начала не может быть равна или позднее даты окончания");
        }
        return service.getAllStats(GetRequestStats.of(start, end, uris, unique));
    }
}
