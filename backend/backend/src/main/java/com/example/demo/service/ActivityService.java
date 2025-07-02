package com.example.demo.service;

import com.example.demo.dto.request.ActivityRequest;
import com.example.demo.dto.response.ActivityDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityService {

    List<ActivityDto> find(LocalDateTime start, LocalDateTime end);

    ActivityDto create(ActivityRequest request);

    ActivityDto update(Long id, ActivityRequest request);

    void delete(Long id);
}