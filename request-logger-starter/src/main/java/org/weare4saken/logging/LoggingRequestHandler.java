package org.weare4saken.logging;

import org.springframework.util.StopWatch;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@FunctionalInterface
public interface LoggingRequestHandler {

    void log(ContentCachingRequestWrapper request,
             ContentCachingResponseWrapper response,
             StopWatch stopWatch);
}
