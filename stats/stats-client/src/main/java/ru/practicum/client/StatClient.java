package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.RequestCreateDto;
import ru.practicum.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatClient extends BaseClient {
    private static final String API_PREFIX = "";

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public RequestDto addRequest(RequestCreateDto requestCreateDto) {
        ResponseEntity<RequestDto> responseEntity = this.post("/hit", requestCreateDto, new RequestDto());
        return responseEntity.getBody();
    }


    public ResponseEntity<Object> getStatsRequest(LocalDateTime start, LocalDateTime end,
                                                  List<String> uris, Boolean unique) {
        System.out.println("stats?start=" + start + "&end=" + end + "&uris=" + uris + "&unique=" + unique);
        return get("stats?start=" + start + "&end=" + end + "&uris=" + uris + "&unique=" + unique);
    }

}
