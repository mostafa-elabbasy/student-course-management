package com.elabbasy.student.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

  @ExceptionHandler({ BusinessException.class })
  public ResponseEntity<Object> handleException(
    BusinessException ex, WebRequest request) throws Exception {

    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage() , 400);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler({ Exception.class })
  public ResponseEntity<Object> handleException(
    Exception ex, WebRequest request) throws Exception {

    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage() , 400);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }
  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<Object> handleException(
    AuthenticationException ex, WebRequest request) throws Exception {

    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage() , 401);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }
}
