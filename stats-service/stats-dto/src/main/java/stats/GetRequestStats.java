package stats;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetRequestStats {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uri;
    private Boolean unique;

    public static GetRequestStats of(LocalDateTime start,
                                     LocalDateTime end,
                                     List<String> uri,
                                     Boolean unique) {
        GetRequestStats request = new GetRequestStats();
        request.setStart(start);
        request.setEnd(start);
        request.setUri(uri == null ? Collections.emptyList() : uri);
        request.setUnique(unique);
        return request;
    }
}
