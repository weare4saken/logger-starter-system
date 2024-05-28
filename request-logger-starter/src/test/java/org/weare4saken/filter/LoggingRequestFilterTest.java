package org.weare4saken.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.StopWatch;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.weare4saken.logging.CommonLoggingRequestHandler;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggingRequestFilterTest {

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    @Mock
    CommonLoggingRequestHandler loggingHandler;

    @InjectMocks
    LoggingRequestFilter loggingRequestFilter;

    @BeforeEach
    void setUp() {
        this.request = new MockHttpServletRequest("GET", "/api/v1/test");
        this.response = new MockHttpServletResponse();
        this.chain = new MockFilterChain();
    }

    @Test
    void doFilterInternal_shouldCallLoggingHandler() throws ServletException, IOException {
        this.loggingRequestFilter.doFilterInternal(this.request, this.response, this.chain);

        verify(this.loggingHandler).log(
                any(ContentCachingRequestWrapper.class),
                any(ContentCachingResponseWrapper.class),
                any(StopWatch.class));
    }

    @Test
    void doFilterInternal_whenFilterChainThrowsError_shouldCallLoggingHandler() throws ServletException, IOException {
        FilterChain mockedFilterChain = Mockito.mock(FilterChain.class);
        doThrow(new RuntimeException("Something went wrong")).when(mockedFilterChain)
                .doFilter(Mockito.any(ServletRequest.class), Mockito.any(ServletResponse.class));

        Assertions.assertThrows(Exception.class,
                () -> this.loggingRequestFilter.doFilterInternal(this.request, this.response, mockedFilterChain));

        verify(this.loggingHandler).log(
                any(ContentCachingRequestWrapper.class),
                any(ContentCachingResponseWrapper.class),
                any(StopWatch.class));
    }
}
