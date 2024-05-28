package org.weare4saken.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.StopWatch;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommonLoggingRequestHandlerTest {

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    MockedStatic<LoggerFactory> mockedLoggerFactory;

    @Mock
    Logger logger;

    CommonLoggingRequestHandler handlerUnderTest;

    @BeforeEach
    void setUp() {
        this.request = new MockHttpServletRequest("GET", "/api/v1/test");
        this.request.addHeader("Accept", "application/json");
        this.request.addHeader("Content-Type", "application/json");
        this.request.addHeader("Authorization", "Bearer test");
        this.request.addHeader("User-Agent", "Mozilla/5.0");
        this.request.addHeader("Accept-Language", "en-US,en;q=0.5");
        this.request.addHeader("Accept-Encoding", "gzip, deflate");
        this.request.addParameter("name", "User");
        this.request.setContent("{\"firstName\": \"Mike\", \"lastName\": \"Johnson\"}".getBytes());
        this.request.setContentType("application/json");
        this.request.setCharacterEncoding("UTF-8");

        this.response = new MockHttpServletResponse();
        this.response.setContentType("application/json");
        this.response.setCharacterEncoding("UTF-8");
        this.response.setStatus(200);
        this.response.setHeader("Cache-Control", "no-cache");
        this.response.addHeader("Access-Control-Allow-Origin", "*");
        this.response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        this.response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        this.response.addHeader("Access-Control-Allow-Credentials", "true");

        this.chain = new MockFilterChain();

        this.mockedLoggerFactory = mockStatic(LoggerFactory.class);
        this.mockedLoggerFactory.when(() -> LoggerFactory.getLogger(CommonLoggingRequestHandler.class))
                .thenReturn(logger);

        this.handlerUnderTest = new CommonLoggingRequestHandler();
    }

    @AfterEach
    void tearDown() {
        this.mockedLoggerFactory.close();
    }

    @Test
    void handleLog_shouldLogRequest() {
        ContentCachingRequestWrapper req = spy(new ContentCachingRequestWrapper(this.request));
        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(this.response);

        byte[] contentAsByteArray = Optional.ofNullable(this.request.getContentAsByteArray()).orElse(new byte[0]);

        StopWatch stopWatch = mock(StopWatch.class);
        ArgumentCaptor<String> captorReqMap = ArgumentCaptor.forClass(String.class);
        doNothing().when(this.logger).info(anyString(), captorReqMap.capture(), anyString());
        when(req.getContentAsByteArray()).thenReturn(contentAsByteArray);

        this.handlerUnderTest.log(req, resp, stopWatch);

        String requestLogMsg = captorReqMap.getValue();
        System.out.println(requestLogMsg);


        String requestTitle = this.request.getMethod() + " " +
                this.request.getRequestURI() + " " +
                this.request.getProtocol();
        assertThat(requestLogMsg).contains("Method: " + requestTitle);
        assertThat(requestLogMsg).contains("Url: " + this.request.getRequestURL());

        for (String header : Collections.list(this.request.getHeaderNames())) {
            assertThat(requestLogMsg).contains(header);
        }

        assertThat(requestLogMsg)
                .contains("Request body: " + new String(req.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    @Test
    void handleLog_shouldLogResponse() {
        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(this.request);
        ContentCachingResponseWrapper resp = spy(new ContentCachingResponseWrapper(this.response));

        byte[] contentAsByteArray = Optional.ofNullable(this.request.getContentAsByteArray()).orElse(new byte[0]);

        StopWatch stopWatch = mock(StopWatch.class);
        ArgumentCaptor<String> captorResMap = ArgumentCaptor.forClass(String.class);
        doNothing().when(this.logger).info(anyString(), anyString(), captorResMap.capture());
        when(stopWatch.getTotalTimeMillis()).thenReturn((long) (Math.random() * 200));
        when(resp.getContentAsByteArray()).thenReturn(contentAsByteArray);

        this.handlerUnderTest.log(req, resp, stopWatch);

        String responseLogMsg = captorResMap.getValue();
        System.out.println(responseLogMsg);

        assertThat(responseLogMsg).contains("Status: " + this.response.getStatus());

        for (String header : this.response.getHeaderNames()) {
            assertThat(responseLogMsg).contains(header);
        }

        assertThat(responseLogMsg)
                .contains("Response body: " + new String(resp.getContentAsByteArray(), StandardCharsets.UTF_8))
                .contains("Total time: " + stopWatch.getTotalTimeMillis() + " ms");
    }
}

