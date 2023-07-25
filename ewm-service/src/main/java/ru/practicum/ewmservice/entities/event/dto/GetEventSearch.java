package ru.practicum.ewmservice.entities.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.entities.event.model.EventState;
import ru.practicum.ewmservice.entities.location.dto.LocationRequestDto;
import ru.practicum.ewmservice.exception.model.IncorrectDateException;

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
    private Boolean hasRange;
    private LocationRequestDto location;

    public static GetEventSearch of(List<Long> users, List<EventState> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    int from, int size, LocationRequestDto location) {
        GetEventSearch request = new GetEventSearch();
        request.setUsers(users == null ? Collections.emptyList() : users);
        request.setStates(states == null ? Collections.emptyList() : states);
        request.setCategories(categories == null ? Collections.emptyList() : categories);
        if (rangeStart == null && rangeEnd == null) {
            request.setHasRange(false);
        } else {
            request.setHasRange(true);
        }
        request.setRangeStart(rangeStart);
        request.setRangeEnd(rangeEnd);
        request.setSize(size);
        request.setFrom(from > 0 ? from / size : 0);
        request.setLocation(checkRequired(location) ? location : null);
        return request;
    }

    public static GetEventSearch of(String text, List<Long> categories, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Boolean onlyAvailable, String sort, int from, int size, LocationRequestDto location) {
        GetEventSearch request = new GetEventSearch();
        request.setText(text == null ? null : text.toLowerCase());
        request.setCategories(categories == null ? Collections.emptyList() : categories);
        if (rangeStart == null && rangeEnd == null) {
            request.setHasRange(false);
        } else {
            request.setHasRange(true);
        }
        request.setPaid(paid);
        checkDate(request.getHasRange(), rangeStart, rangeEnd);
        request.setRangeStart(rangeStart);
        request.setRangeEnd(rangeEnd);
        request.setOnlyAvailable(onlyAvailable);
        request.setSort(sort == null ? EventSort.UNSORTED : convertSort(sort));
        request.setSize(size);
        request.setFrom(from > 0 ? from / size : 0);
        request.setLocation(checkRequired(location) ? location : null);
        return request;
    }

    private static EventSort convertSort(String sort) {
        try {
            return EventSort.valueOf(sort.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkDate(Boolean hasRange, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (hasRange && (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd))) {
            throw new IncorrectDateException("Дата начала не может быть равна или позднее даты окончания");
        }
    }

    private static boolean checkRequired(LocationRequestDto dto) {
        if (dto.getLat() == null && dto.getLon() == null && dto.getRad() == null) {
            return false;
        }
        return true;
    }
}
