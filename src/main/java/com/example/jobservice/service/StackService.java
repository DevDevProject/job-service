package com.example.jobservice.service;

import com.example.jobservice.dto.stack.response.PopularStackResponseDto;
import com.example.jobservice.dto.stack.response.StackCountDto;
import com.example.jobservice.mapper.StackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StackService {

    private final StackMapper stackMapper;

    public PopularStackResponseDto findPopular() {
        return new PopularStackResponseDto(stackMapper.findPopularStacks(), stackMapper.findTotalCount());
    }
}
