package com.example.springtour.config.audit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("audit.rest")
@Data
public class AuditProperties {

    private boolean enabled;
    private int logBodyMaxSize = 2048;
    private Direction in = new Direction();

}

@Data
class Direction {
    private List<String> includedPaths = new ArrayList<>();
    private List<String> excludedPaths = new ArrayList<>();
    private List<String> sensitiveKeys = new ArrayList<>();
}