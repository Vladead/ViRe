package com.vire.virebackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        Meta meta
) {
    public record Meta(
            @JsonProperty("page")
            Integer page,

            @JsonProperty("size")
            Integer size,

            @JsonProperty("total_elements")
            Long totalElements,

            @JsonProperty("total_pages")
            Integer totalPages,

            @JsonProperty("has_next")
            Boolean hasNext,

            @JsonProperty("has_previous")
            Boolean hasPrevious,

            @JsonProperty("next")
            String next,

            @JsonProperty("prev")
            String prev
    ) {
    }
}
