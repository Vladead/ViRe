package com.vire.virebackend.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        Meta meta
) {
    public record Meta(
            Integer page,
            Integer size,
            Long totalElements,
            Integer totalPages,
            Boolean hasNext,
            Boolean hasPrevious,
            String next,
            String prev
    ) {
    }
}
