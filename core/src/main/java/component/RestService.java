package component;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@AllArgsConstructor
@Slf4j
public class RestService {
    private static final String HOST = "https://localhost:";
    private final RestTemplateBuilder restTemplateBuilder;

    public <T> ResponseEntity<T> doGetRequest(String url, Class<T> responseClass) {
        String fullUrl = HOST + url;
        log.info("GET request: {}", fullUrl);
        return restTemplateBuilder.build().getForEntity(fullUrl, responseClass);
    }

    public <T> ResponseEntity<T> doPostRequest(String url, @NonNull Object obj, Class<T> responseClass) {
        String fullUrl = HOST + url;
        HttpEntity<?> request = new HttpEntity<>(obj);
        log.info("POST request: {} : {}", fullUrl, request);
        return restTemplateBuilder.build().postForEntity(fullUrl, obj, responseClass);
    }
}
