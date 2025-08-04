package com.vikash.esd_final_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        @JsonProperty("content") List<T> content,

        @JsonProperty("page_number") int pageNumber,

        @JsonProperty("page_size") int pageSize,

        @JsonProperty("total_elements") long totalElements,

        @JsonProperty("total_pages") int totalPages,

        @JsonProperty("is_first") boolean isFirst,

        @JsonProperty("is_last") boolean isLast,

        @JsonProperty("has_next") boolean hasNext,

        @JsonProperty("has_previous") boolean hasPrevious) {
    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious());
    }
}
