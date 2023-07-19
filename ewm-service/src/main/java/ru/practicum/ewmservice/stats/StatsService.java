package ru.practicum.ewmservice.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.entities.event.model.Event;
import ru.practicum.statsclient.client.StatsClient;
import stats.GetRequestStats;
import stats.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private final StatsClient client;
    private final ObjectMapper mapper;

    @Autowired
    public StatsService(@Value("${stats-server.url}") String serverUrl,
                        @Value("${spring.application.name}") String applicationName, ObjectMapper mapper) {
        client = new StatsClient(serverUrl, applicationName);
        this.mapper = mapper;
    }


    public void makeHit(HttpServletRequest request) {
        client.makeHit(request);
    }

    public List<ViewStats> getStats(GetRequestStats request) {
        ResponseEntity<Object> allStats = client.getAllStats(request);
        if (allStats.hasBody() && allStats.getStatusCode().is2xxSuccessful()) {
            List<ViewStats> viewStats = mapper.convertValue(allStats.getBody(), new TypeReference<>() {
            });
            return viewStats;
        } else {
            return Collections.emptyList();
        }
    }

    public List<ViewStats> getStats(HttpServletRequest servletRequest, LocalDateTime publishedOn) {
        GetRequestStats request = GetRequestStats.of(publishedOn, publishedOn.plusWeeks(3), List.of(servletRequest.getRequestURI()), true);
        return getStats(request);
    }

    public Map<Long, Integer> getHits(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        LocalDateTime startDate = events.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .get();
        List<String> uris = events.stream().map(event -> String.format("/events/%s", event.getId())).collect(Collectors.toList());

        GetRequestStats request = GetRequestStats.of(startDate, LocalDateTime.now(), uris, true);
        Map<Long, Integer> viewMap = getStats(request).stream()
                .collect(Collectors.toMap(v -> Long.parseLong(v.getUri().replaceFirst("/events/", "")),
                        ViewStats::getHits));
        return viewMap;
    }

    public Optional<ViewStats> getStatBySingleUri(HttpServletRequest servletRequest, LocalDateTime publishedOn) {
        return getStats(servletRequest, publishedOn).stream().findFirst();
    }
}
