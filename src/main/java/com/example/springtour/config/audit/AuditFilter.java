package com.example.springtour.config.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class AuditFilter extends OncePerRequestFilter {

    private final ApplicationEventPublisher publisher;
    private final AuditUtils auditUtils;

    @Value("${spring.application.name}")
    private String applicationName;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        if (!auditUtils.shouldAuditIncome(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedReq = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(response);

        Instant start = Instant.now();
        String errorMessage = null;

        AuditEntry auditEntry = AuditEntry.builder()
                .service(applicationName)
                .direction("IN")
                .method(wrappedReq.getMethod())
                .url(auditUtils.getOriginalUri(wrappedReq))
                .username(wrappedReq.getHeader("X-User-Id"))
                .headers(Collections.list(wrappedReq.getHeaderNames()).stream()
                        .collect(Collectors.toMap(h -> h, wrappedReq::getHeader)))
                .timestamp(start)
                .build();

        try {
            // Execute the chain with wrapped request/response
            chain.doFilter(wrappedReq, wrappedRes);
            // After chain execution, the content is available
            String requestBody = auditUtils.extractRequestBody(wrappedReq);
            String responseBody = auditUtils.extractResponseBody(wrappedReq, wrappedRes);
            int status = wrappedRes.getStatus();
            auditEntry.setRequestBody(auditUtils.trimAndMask(requestBody));
            auditEntry.setResponseBody(auditUtils.trimAndMask(responseBody));
            auditEntry.setDuration(Instant.now().toEpochMilli() - start.toEpochMilli());
            auditEntry.setResponseStatus(status);

            publisher.publishEvent(new AuditEvent(this, auditEntry));

        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            log.error("Error in filter chain: {}", ex.getMessage());

            // Create audit entry for error case
            AuditEntry errorAuditEntry = AuditEntry.builder()
                    .service(applicationName)
                    .direction("IN")
                    .method(wrappedReq.getMethod())
                    .url(auditUtils.getOriginalUri(wrappedReq))
                    .username(wrappedReq.getHeader("X-User-Id"))
                    .headers(Collections.list(wrappedReq.getHeaderNames()).stream()
                            .collect(Collectors.toMap(h -> h, wrappedReq::getHeader)))
                    .requestBody(auditUtils.trimAndMask(auditUtils.extractRequestBody(wrappedReq)))
                    .error(errorMessage)
                    .timestamp(start)
                    .duration(Instant.now().toEpochMilli() - start.toEpochMilli())
                    .build();

            publisher.publishEvent(new AuditEvent(this, errorAuditEntry));
            throw ex;
        } finally {
            // Always copy response body back
            wrappedRes.copyBodyToResponse();
        }



    }

}
