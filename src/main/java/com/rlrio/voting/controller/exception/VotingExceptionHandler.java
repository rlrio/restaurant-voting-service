package com.rlrio.voting.controller.exception;

import com.rlrio.voting.service.exception.VotingException;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.rlrio.voting.controller.util.WebUtil.getErrorResponse;

@RestControllerAdvice
public class VotingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowable(Throwable throwable, WebRequest request) {
        logger.debug("unknown error", throwable);
        return new ResponseEntity<>(getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, throwable.getMessage()),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VotingException.class)
    public ResponseEntity<Object> handleVotingException(VotingException exception, WebRequest request) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.BAD_REQUEST, request, exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var errorMessage = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(getErrorResponse(HttpStatus.BAD_REQUEST, request, errorMessage),
                HttpStatus.BAD_REQUEST);
    }
}
