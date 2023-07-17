package ru.practicum.ewmservice.stats;

import org.springframework.stereotype.Service;
import ru.practicum.statsclient.client.StatsClient;

@Service
public class StatsService {
    private final StatsClient client = new StatsClient("http://localhost:9090", "ewm-service");
}
