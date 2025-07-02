package com.example.demo.repository;

import com.example.demo.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a WHERE a.startTime < :end AND (a.endTime IS NULL OR a.endTime > :start)")
    List<Activity> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
