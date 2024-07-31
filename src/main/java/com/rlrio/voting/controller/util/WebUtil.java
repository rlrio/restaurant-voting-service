package com.rlrio.voting.controller.util;

import com.rlrio.voting.controller.dto.auth.ErrorResponse;
import java.time.OffsetDateTime;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@UtilityClass
public class WebUtil {

    public static ErrorResponse getErrorResponse(HttpStatus status, WebRequest request, String errorMessage) {
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
}
