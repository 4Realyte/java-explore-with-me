package ru.practicum.ewmservice.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.statsclient.client.StatsClient;
import stats.GetRequestStats;
import stats.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Service
public class StatsService {
    private final StatsClient client;

    @Autowired
    public StatsService(@Value("${stats-server.url}") String serverUrl,
                        @Value("${spring.application.name}") String applicationName) {
        client = new StatsClient(serverUrl, applicationName);
    }


    public void makeHit(HttpServletRequest request) {
        client.makeHit(request);
    }

    public List<ViewStats> getStats(GetRequestStats request) {
        ResponseEntity<Object> allStats = client.getAllStats(request);
        if (allStats.hasBody() && allStats.getStatusCode().is2xxSuccessful()) {
            return (List<ViewStats>) client.getAllStats(request).getBody();
        } else {
            return Collections.emptyList();
        }
    }
}
