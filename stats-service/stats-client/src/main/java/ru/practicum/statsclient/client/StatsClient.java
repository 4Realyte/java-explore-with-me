package ru.practicum.statsclient.client;

import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplateHandler;
import stats.EndpointHit;
import stats.GetRequestStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatsClient {
    private final RestTemplate restTemplate;
    private final String application;
    private final String baseUri;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm:ss");


    public StatsClient(String serverUrl, String apiPrefix, String application) {
        this.baseUri = serverUrl + apiPrefix;
        this.application = application;
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory(baseUri);
        restTemplate.setUriTemplateHandler(uriTemplateHandler);
    }

    public StatsClient(String serverUrl, String application) {
        this.baseUri = serverUrl;
        this.application = application;
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory(serverUrl);
        restTemplate.setUriTemplateHandler(uriTemplateHandler);
    }

    public void makeHit(HttpServletRequest request) {
        EndpointHit hit = EndpointHit.builder()
                .app(application)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        HttpEntity<EndpointHit> httpEntity = new HttpEntity<>(hit, defaultHeaders());

        try {
            restTemplate.exchange("/hit", HttpMethod.POST, httpEntity, Void.class);
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Object> getAllStats(GetRequestStats request) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, defaultHeaders());
        Map<String, Object> parameters = getParameters(request);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUri + "/stats");
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        ResponseEntity<Object> response;
        try {
            response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, Object.class, parameters);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareResponse(response);
    }

    private Map<String, Object> getParameters(GetRequestStats request) {
        Map<String, Object> parameters = new HashMap<>(Map.of("start", request.getStart().format(formatter),
                "end", request.getEnd().format(formatter)));
        if (!request.getUris().isEmpty()) {
            parameters.put("uris", request.getUris());
        }
        parameters.put("unique", request.getUnique());
        return parameters;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
