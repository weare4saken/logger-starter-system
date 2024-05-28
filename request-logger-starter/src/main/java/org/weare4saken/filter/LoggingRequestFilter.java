package org.weare4saken.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.weare4saken.logging.LoggingRequestHandler;

import java.io.IOException;

public class LoggingRequestFilter extends OncePerRequestFilter {

    private final LoggingRequestHandler loggingRequestHandler;

    @Autowired
    public LoggingRequestFilter(LoggingRequestHandler loggingRequestHandler) {
        this.loggingRequestHandler = loggingRequestHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ContentCachingRequestWrapper req = this.wrapRequest(request);
        ContentCachingResponseWrapper resp = this.wrapResponse(response);

        try {
            filterChain.doFilter(req, resp);
        } finally {
            stopWatch.stop();
            this.loggingRequestHandler.log(req, resp, stopWatch);
            resp.copyBodyToResponse();
        }
    }

    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}
