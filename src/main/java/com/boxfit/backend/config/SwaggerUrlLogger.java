package com.boxfit.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SwaggerUrlLogger {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        String baseUrl = "http://localhost:" + serverPort + contextPath;
        String separator = "============================================";
        String message1 = "🚀 Application started successfully!";
        String message2 = "📚 Swagger UI: " + baseUrl + "/swagger-ui/index.html";
        String message3 = "📄 API Docs: " + baseUrl + "/v3/api-docs";

        log.info(separator);
        log.info(message1);
        log.info(separator);
        log.info(message2);
        log.info(message3);
        log.info(separator);

        // Ensure output is visible even if logging is not properly configured
        System.out.println(separator);
        System.out.println(message1);
        System.out.println(separator);
        System.out.println(message2);
        System.out.println(message3);
        System.out.println(separator);
    }
}

