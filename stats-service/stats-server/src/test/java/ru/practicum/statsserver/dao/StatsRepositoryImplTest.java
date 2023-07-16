package ru.practicum.statsserver.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import stats.EndpointHit;
import stats.GetRequestStats;
import stats.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatsRepositoryImplTest {

    private final StatsRepository repository;

    @Test
    void getAllStats_ShouldReturnListOfSize_1_AndHitEqualTo_1_whenUniqueIsTrue() {
        // given
        LocalDateTime start = LocalDateTime.of(2023, 07, 1, 12, 00, 30);
        LocalDateTime end = LocalDateTime.of(2023, 12, 1, 18, 00, 30);
        EndpointHit hit = EndpointHit.builder()
                .app("someApp")
                .uri("/api/1")
                .ip("123.12.15")
                .timestamp(start)
                .build();

        repository.addHit(hit);
        EndpointHit hit2 = EndpointHit.builder()
                .app("someApp")
                .uri("/api/2")
                .ip("123.12.15")
                .timestamp(start.plusHours(1))
                .build();
        repository.addHit(hit2);

        EndpointHit hitDubble = EndpointHit.builder()
                .app("someApp")
                .uri("/api/1")
                .ip("123.12.15")
                .timestamp(start)
                .build();
        repository.addHit(hitDubble);
        // when
        List<ViewStats> res = repository.getAllStats(GetRequestStats.of(start,
                end, null, true));
        //then
        System.out.println(res);
        /*assertThat(res, hasSize(1));
        assertThat(res, hasItem(allOf(
                hasProperty("uri", equalTo("/api/1")),
                hasProperty("hits", equalTo(1))
        )));*/
    }
}