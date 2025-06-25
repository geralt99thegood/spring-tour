package com.example.springtour.config.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    private final AuditUtils auditUtils;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ApplicationEventPublisher eventPublisher) {
        RestTemplate restTemplate = builder.build();
        // Wrap the factory to buffer the body
        restTemplate.setRequestFactory(
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
        );

        restTemplate.getInterceptors().add((req, body, exec) -> {
            if (!auditUtils.restAudit()) {
                return exec.execute(req, body);
            }

            Instant start = Instant.now();
            ClientHttpResponse res = exec.execute(req, body);

            // Now you can safely read the body multiple times
            byte[] responseBody = StreamUtils.copyToByteArray(res.getBody());

            AuditEntry entry = AuditEntry.builder()
                    .direction("OUT")
                    .service(applicationName)
                    .username(req.getHeaders().getFirst("X-User-Id"))
                    .timestamp(start)
                    .duration(Duration.between(start, Instant.now()).toMillis())
                    .method(req.getMethod().name())
                    .url(req.getURI().toString())
                    .headers(req.getHeaders().toSingleValueMap())
                    .requestBody(new String(body, StandardCharsets.UTF_8))
                    .responseBody(new String(responseBody, StandardCharsets.UTF_8))
                    .responseStatus(res.getStatusCode().value())
                    .build();

            eventPublisher.publishEvent(new AuditEvent(this, entry));

            // Return the same response (buffered body is reusable)
            return res;
        });


        return restTemplate;
    }

}
