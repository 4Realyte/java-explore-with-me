package ru.practicum.ewmservice.entities.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.entities.event.model.EventState;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetEventSearch {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private int from;
    private int size;
    private EventSort sort;
    private Boolean onlyAvailable;
    private Boolean paid;
    private String text;

    public static GetEventSearch of(List<Long> users, List<EventState> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    int from, int size) {
        GetEventSearch request = new GetEventSearch();
        request.setUsers(users == null ? Collections.emptyList() : users);
        request.setStates(states == null ? Collections.emptyList() : states);
        request.setCategories(categories == null ? Collections.emptyList() : categories);
        request.setRangeStart(rangeStart);
        request.setRangeEnd(rangeEnd);
        request.setSize(size);
        request.setFrom(from > 0 ? from / size : 0);
        return request;
    }

    public static GetEventSearch of(String text, List<Long> categories, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Boolean onlyAvailable, String sort, int from, int size) {
        GetEventSearch request = new GetEventSearch();
        request.setText(text == null ? null : text.toLowerCase());
        request.setCategories(categories == null ? Collections.emptyList() : categories);
        request.setPaid(paid);
        request.setRangeStart(rangeStart);
        request.setRangeEnd(rangeEnd);
        request.setOnlyAvailable(onlyAvailable);
        request.setSort(sort == null ? EventSort.UNSORTED : convertSort(sort));
        request.setSize(size);
        request.setFrom(from > 0 ? from / size : 0);
        return request;
    }

    private static EventSort convertSort(String sort) {
        try {
            return EventSort.valueOf(sort.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
