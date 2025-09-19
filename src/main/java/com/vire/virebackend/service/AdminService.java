package com.vire.virebackend.service;

import com.vire.virebackend.dto.admin.UserSummaryDto;
import com.vire.virebackend.mapper.UserSummaryMapper;
import com.vire.virebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public Page<UserSummaryDto> list(Pageable pageable) {
        var effective = normalize(pageable);
        var page = userRepository.findAll(effective);

        return page.map(UserSummaryMapper::toDto);
    }

    public Pageable normalize(Pageable pageable) {
        var size = Math.min(Math.max(pageable.getPageSize(), 1), 100); // 1..100
        var sort = pageable.getSort().isUnsorted()
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : pageable.getSort();
        return PageRequest.of(pageable.getPageNumber(), size, sort);
    }
}
