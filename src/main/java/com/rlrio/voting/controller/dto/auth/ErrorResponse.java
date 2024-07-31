package com.rlrio.voting.controller.dto.auth;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorResponse {
    private int status;
    private String message;
    private String uri;
    private String method;
    private String queryParams;
    private OffsetDateTime timestamp;
}
