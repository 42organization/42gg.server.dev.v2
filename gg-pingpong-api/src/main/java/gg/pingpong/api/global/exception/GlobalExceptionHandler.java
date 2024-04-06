package gg.pingpong.api.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.ErrorResponse;
import gg.utils.exception.custom.AuthenticationException;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.CustomRuntimeException;
import gg.utils.exception.custom.DBConsistencyException;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.exception.custom.PageNotFoundException;
import lombok.extern.slf4j.Slf4j;

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

	@ExceptionHandler({DBConsistencyException.class})
	public ResponseEntity<ErrorResponse> dbConsistencyException(DBConsistencyException ex) {
		log.error("db 정합성 오류", ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getErrorCode()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({NotExistException.class, PageNotFoundException.class})
	public ResponseEntity<ErrorResponse> notFoundException(CustomRuntimeException ex) {
		log.error("Not Exist", ex);
		ErrorResponse response = new ErrorResponse(ex.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({DuplicationException.class})
	public ResponseEntity<ErrorResponse> duplicatedException(DuplicationException ex) {
		log.error("Duplicated", ex);
		return new ResponseEntity<>(new ErrorResponse(ex.getErrorCode()), HttpStatus.CONFLICT);
	}

	@ExceptionHandler({ForbiddenException.class})
	public ResponseEntity<ErrorResponse> forbiddenException(ForbiddenException ex) {
		log.error("forbidden", ex);
		ErrorResponse response = new ErrorResponse(ex.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({CustomRuntimeException.class})
	public ResponseEntity<ErrorResponse> validException(CustomRuntimeException ex) {
		log.error("예외처리된 에러", ex.getMessage(), ex.getErrorCode());
		ErrorResponse response = new ErrorResponse(ex.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException ex) {
		log.error("authentication exception");
		ErrorResponse response = new ErrorResponse(ex.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	@ExceptionHandler(AmazonServiceException.class)
	protected ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedExceptionHandle(AmazonServiceException ex) {
		log.error("AmazonServiceException", ex);
		ErrorResponse response = new ErrorResponse(ErrorCode.AWS_S3_ERR);
		return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode()));
	}

	@ExceptionHandler(SdkClientException.class)
	protected ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedExceptionHandle(SdkClientException ex) {
		log.error("AmazonServiceException", ex.getMessage());
		ErrorResponse response = new ErrorResponse(ErrorCode.AWS_SERVER_ERR);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({RuntimeException.class})
	public ResponseEntity<ErrorResponse> runtimeException(RuntimeException ex) {
		log.error("처리되지 않은 에러입니다.", ex);
		ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERR);
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedExceptionHandle(
		HttpRequestMethodNotSupportedException ex) {
		log.error("지원하지 않는 메소드 요청입니다.", ex.getMethod());
		ErrorResponse response = new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
		return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("!!!!!! SERVER ERROR !!!!!!", ex.getMessage());
		ex.printStackTrace();
		ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERR);
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity handleException(HttpMessageNotReadableException exception) {
		return ResponseEntity.badRequest().body(ErrorCode.UNREADABLE_HTTP_MESSAGE.getMessage());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity handleException(MissingServletRequestParameterException exception) {
		return ResponseEntity.badRequest().body(ErrorCode.BAD_ARGU.getMessage());
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity handleException(BusinessException exception) {
		log.error("SERVER BUSINESS EXCEPTION", exception.getMessage());
		exception.printStackTrace();
		ErrorResponse response = new ErrorResponse(exception.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}
}
