package com.promineotech.jeep.errorhandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class GlobalErrorHandler {
  
  Logger log = LoggerFactory.getLogger(getClass());
  
  private enum LogStatus {
    STACK_TRACE, MESSAGE
  }
  
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public Map<String, Object> handleMethodArgumentTypeMismatchException (MethodArgumentTypeMismatchException e, WebRequest webRequest) {
    return createExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE);
  }
  
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public Map<String, Object> handleConstraintViolationException (ConstraintViolationException e, WebRequest webRequest) {
    return createExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE);
  }
  
  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNoSuchElementException(NoSuchElementException e, WebRequest webRequest) {
    return createExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE);
  }
  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleException(Exception e, WebRequest webRequest) {
    return createExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR, webRequest, LogStatus.STACK_TRACE);
  }
  
  private Map<String, Object> createExceptionMessage(Exception e,
      HttpStatus status, WebRequest webRequest, LogStatus logStatus ) {
    Map<String, Object> error = new HashMap<>();
    String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    
    if(webRequest instanceof ServletWebRequest) {
      error.put("uri", ((ServletWebRequest)webRequest).getRequest().getRequestURI());
    }
    
    error.put("message", e.toString());
    error.put("status code", status.value());

    error.put("timestamp", timestamp);
    error.put("reason", status.getReasonPhrase());
    
    if(logStatus == LogStatus.MESSAGE) {
      log.error("Exception: {}", e.toString());
    } else {
      log.error("Exception: {}", e);
    }
    
    return error;
  }
}
