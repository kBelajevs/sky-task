package com.sky.kbelajevs.task.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);


    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            return new String(contentAsByteArray, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Failure in Logging filter", e);
        }
        return "";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var requestWrapper = new ContentCachingRequestWrapper(request);
        var responseWrapper = new ContentCachingResponseWrapper(response);

        var requestBody = getStringValue(requestWrapper.getContentAsByteArray(),
                request.getCharacterEncoding());
        LOGGER.info(
                "STARTED PROCESSING : METHOD={}; REQUESTURI={}; REQUEST PAYLOAD={}",
                request.getMethod(), request.getRequestURI(), requestBody);
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        long timeTaken = System.currentTimeMillis() - startTime;


        var responseBody = getStringValue(responseWrapper.getContentAsByteArray(),
                response.getCharacterEncoding());

        LOGGER.info(
                "FINISHED PROCESSING : METHOD={}; REQUESTURI={}; RESPONSE CODE={}; RESPONSE={}; TIME TAKEN={}",
                request.getMethod(), request.getRequestURI(), response.getStatus(), responseBody,
                timeTaken);
        responseWrapper.copyBodyToResponse();
    }
}
