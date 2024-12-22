package com.rlrio.voting.controller.exception;

import com.rlrio.voting.service.exception.NotFoundException;
import com.rlrio.voting.service.exception.VotingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.rlrio.voting.controller.util.WebUtil.getConstraintErrors;
import static com.rlrio.voting.controller.util.WebUtil.getErrorResponse;

@RestControllerAdvice
public class VotingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowable(Throwable throwable, WebRequest request) {
        logger.debug("unknown error", throwable);
        return new ResponseEntity<>(getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, throwable.getMessage()),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException exception, WebRequest request) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED, request, exception.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception, WebRequest request) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.NOT_FOUND, request, exception.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException exception, WebRequest request) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.BAD_REQUEST, request, exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VotingException.class)
    public ResponseEntity<Object> handleVotingException(VotingException exception, WebRequest request) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.BAD_REQUEST, request, exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var body = getErrorResponse(status, request, getConstraintErrors(ex.getBindingResult()));
        return new ResponseEntity<>(body, status);
    }
}
