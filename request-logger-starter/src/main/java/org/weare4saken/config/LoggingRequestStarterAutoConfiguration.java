package org.weare4saken.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.weare4saken.filter.LoggingRequestFilter;
import org.weare4saken.logging.CommonLoggingRequestHandler;
import org.weare4saken.logging.LoggingRequestHandler;

@AutoConfiguration
@EnableConfigurationProperties(LoggingRequestStarterProperties.class)
@ConditionalOnProperty(prefix = "request-logger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingRequestStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LoggingRequestHandler loggingRequestHandler() {
        return new CommonLoggingRequestHandler();
    }

    @Bean
    public LoggingRequestFilter loggingRequestFilter(LoggingRequestHandler loggingRequestHandler) {
        return new LoggingRequestFilter(loggingRequestHandler);
    }
}
