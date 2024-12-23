package com.rlrio.voting.controller.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageResponse<T> extends PageFilter {
    private int totalPages;
    private long totalSize;
    private List<T> content;
}
