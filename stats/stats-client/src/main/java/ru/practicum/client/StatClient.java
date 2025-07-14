package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.RequestCreateDto;

import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.RequestOutputDto;

@Service
public class StatClient {

    private final WebClient webClient;

    @Autowired
    public StatClient(@Value("${stats-server.url}") String statsServerUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(statsServerUrl)
                .build();
    }

    public void addRequest(RequestCreateDto requestDto) {
        webClient.post().uri("/hit").bodyValue(requestDto).retrieve().bodyToMono(Object.class).block();
    }

    public ResponseEntity<List<RequestOutputDto>> getStatsRequest(String start,
                                                                  String end,
                                                                  List<String> uris,
                                                                  Boolean unique) {

        ResponseEntity<List<RequestOutputDto>> response = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end);
                    if (uris != null)
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    if (unique != null)
                        uriBuilder.queryParam("unique", unique);
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntityList(RequestOutputDto.class)
                .block();
        return response;
    }

}
