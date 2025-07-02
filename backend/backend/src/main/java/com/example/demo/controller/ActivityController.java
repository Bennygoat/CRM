package com.example.demo.controller;

import com.example.demo.dto.request.ActivityRequest;
import com.example.demo.dto.response.ActivityDto;
import com.example.demo.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;


@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }


    @GetMapping
    public ResponseEntity<List<ActivityDto>> getActivities(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startTime = parseDateTime(start, true);
        LocalDateTime endTime = parseDateTime(end, false);

        List<ActivityDto> activities = activityService.find(startTime, endTime);
        return ResponseEntity.ok(activities);
    }

    // --- 輔助方法，用來解析日期時間字串 ---
    private LocalDateTime parseDateTime(String dateTimeString, boolean isStart) {
        try {
            // 優先嘗試解析為完整的日期時間
            return LocalDateTime.parse(dateTimeString);
        } catch (DateTimeParseException e) {
            // 如果失敗，則嘗試解析為僅日期
            LocalDate date = LocalDate.parse(dateTimeString);
            // 如果是開始時間，則設為一天的開始；如果是結束時間，則設為一天的結束。
            return isStart ? date.atStartOfDay() : date.atTime(23, 59, 59);
        }
    }

    @PostMapping
    public ResponseEntity<ActivityDto> create(@Valid @RequestBody ActivityRequest request) {
        ActivityDto createdActivity = activityService.create(request);
        return new ResponseEntity<>(createdActivity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDto> update(@PathVariable Long id, @Valid @RequestBody ActivityRequest request) {
        ActivityDto updatedActivity = activityService.update(id, request);
        return ResponseEntity.ok(updatedActivity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
