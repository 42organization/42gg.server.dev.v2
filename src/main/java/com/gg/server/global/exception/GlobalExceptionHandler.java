package com.gg.server.global.exception;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.gg.server.global.exception.custom.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BindException.class})
    public ResponseEntity<ErrorResponse> validException(BindException ex) {
        log.error("bind error", ex.getBindingResult().getAllErrors().get(0));
        ErrorCode ec = ErrorCode.VALID_FAILED;
        ec.setMessage(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        ErrorResponse response = new ErrorResponse(ec);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> typeErrorException(MethodArgumentTypeMismatchException ex) {
        log.error("type error", ex.getMessage());
        ErrorCode ec = ErrorCode.VALID_FAILED;
        ec.setMessage(ex.getMessage());
        ErrorResponse response = new ErrorResponse(ec);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CustomRuntimeException.class})
    public ResponseEntity<ErrorResponse> validException(CustomRuntimeException ex) {
        log.error("예외처리된 에러", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AmazonServiceException.class)
    protected ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedExceptionHandle(AmazonServiceException ex) {
        log.error("AmazonServiceException", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.AWS_S3_ERR);
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(SdkClientException.class)
    protected ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedExceptionHandle(SdkClientException ex) {
        log.error("AmazonServiceException", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.AWS_SERVER_ERR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException ex) {
        log.error("Runtime error", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedExceptionHandle(HttpRequestMethodNotSupportedException ex) {
        log.error("지원하지 않는 메소드 요청입니다.", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("처리되지 않은 에러입니다.", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
