package java.com.example.springtour.config.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditUtils {

    private final AuditProperties props;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public boolean shouldAuditIncome(String requestURI) {
        return props.isEnabled() &&
                props.getIn().getIncludedPaths().stream().anyMatch(p -> matcher.match(p, requestURI)) &&
                props.getIn().getExcludedPaths().stream().noneMatch(p -> matcher.match(p, requestURI));
    }


    public String extractRequestBody(HttpServletRequest req) {
        if (req.getContentType() != null && req.getContentType().startsWith("multipart")) {
            return "[multipart skipped]";
        }

        ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(req, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            return new String(buf, StandardCharsets.UTF_8);
        }
        return "[request wrapper not present]";
    }


    public String extractResponseBody(HttpServletRequest req, ContentCachingResponseWrapper res) throws IOException {
        if (req.getContentType() != null && req.getContentType().startsWith("multipart")) {
            return "[multipart skipped]";
        }
        res.flushBuffer();
        byte[] buf = res.getContentAsByteArray();
        try {
            return new String(buf, res.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return "";
        }
    }

    public String safeToJson(Object obj) {
        if (obj == null) return "";
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[unserializable]";
        }
    }

    public String trimAndMask(String body) {
        if (body == null) return "";
        if (body.length() > props.getLogBodyMaxSize()) body = "[TRUNCATED]";
        for (String key : props.getIn().getSensitiveKeys()) {
            body = body.replaceAll("(?i)\"" + key + "\"\\s*:\\s*\".*?\"", "\"" + key + "\":\"[MASKED]\"");
        }
        return body;
    }

    public String getOriginalUri(HttpServletRequest request) {
        String original = (String) request.getAttribute("jakarta.servlet.forward.request_uri");
        if (original == null) {
            original = (String) request.getAttribute("javax.servlet.forward.request_uri");
        }
        return original != null ? original : request.getRequestURI();
    }

    public boolean restAudit() {
        return props.isEnabled();
    }

    public void writeAudit(AuditEntry entry) {
        log.info("#### AUDIT [{}][{}] ########################################################" +
                "\n ## {} {} -  status {} at {} - duration={}ms" +
                "\n ## Headers: {}" +
                "\n ## Request: {}" +
                "\n ## Response: {}" +
                "\n ## Error: {}" +
                "\n #########################################################################",
                entry.getDirection(), entry.getService(),
                entry.getMethod(), entry.getUrl(), entry.getResponseStatus(), entry.getTimestamp(), entry.getDuration(),
                entry.getHeaders(),
                entry.getRequestBody(),
                entry.getResponseBody(),
                entry.getError()
                );
    }


}
