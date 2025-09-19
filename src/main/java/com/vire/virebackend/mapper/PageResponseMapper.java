package com.vire.virebackend.mapper;

import com.vire.virebackend.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class PageResponseMapper {

    private PageResponseMapper() {
    }

    public static <T> PageResponse<T> toResponse(Page<T> page, HttpServletRequest http) {
        var uri = ServletUriComponentsBuilder.fromRequestUri(http);

        var next = page.hasNext()
                ? uri.replaceQueryParam("page", page.getNumber() + 1)
                .replaceQueryParam("size", page.getSize()).toUriString()
                : null;

        var prev = page.hasPrevious()
                ? uri.replaceQueryParam("page", page.getNumber() - 1)
                .replaceQueryParam("size", page.getSize()).toUriString()
                : null;

        var meta = new PageResponse.Meta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                next,
                prev
        );

        return new PageResponse<>(page.getContent(), meta);
    }
}
