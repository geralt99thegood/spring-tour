package com.example.springtour.config.audit;

import lombok.Data;

import java.util.List;

@Data
public class AuditItemProperties {

    private List<String> includedPaths;
    private List<String> excludedPaths;
    private List<String> sensitiveKeys;
    private int logBodyMaxSize = 2048;
}
