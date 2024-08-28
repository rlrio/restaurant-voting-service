package com.rlrio.voting.controller.util;

import com.rlrio.voting.controller.dto.auth.ErrorResponse;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@UtilityClass
public class WebUtil {

    public static ErrorResponse getErrorResponse(HttpStatusCode status, WebRequest request, String errorMessage) {
        var errorDto = new ErrorResponse();
        errorDto.setStatus(status.value())
                .setTimestamp(OffsetDateTime.now())
                .setMessage(errorMessage);
        if (request instanceof ServletWebRequest servletRequest) {
            errorDto.setUri(servletRequest.getRequest().getRequestURI())
                    .setMethod(servletRequest.getRequest().getMethod())
                    .setQueryParams(servletRequest.getRequest().getQueryString());
        }
        return errorDto;
    }

    public static String getConstraintErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
