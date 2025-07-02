package com.example.demo.service.impl;

import com.example.demo.dto.request.ActivityRequest;
import com.example.demo.dto.response.ActivityDto;
import com.example.demo.entity.Activity;
import com.example.demo.entity.Contact;
import com.example.demo.entity.Opportunity;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.service.ActivityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final OpportunityRepository opportunityRepository;
    private final ContactRepository contactRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository,
                               OpportunityRepository opportunityRepository,
                               ContactRepository contactRepository) {

        this.activityRepository = activityRepository;
        this.opportunityRepository = opportunityRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityDto> find(LocalDateTime start, LocalDateTime end) {

        List<Activity> activities = activityRepository.findByDateRange(start, end);
        return activities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActivityDto create(ActivityRequest request) {

        Opportunity opportunity = (request.getOpportunityId() != null)
                ? opportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new EntityNotFoundException("關聯商機失敗：找不到商機 ID " + request.getOpportunityId()))
                : null;

        Contact contact = (request.getContactId() != null)
                ? contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new EntityNotFoundException("關聯聯絡人失敗：找不到聯絡人 ID " + request.getContactId()))
                : null;

        Activity activity = Activity.builder()
                .title(request.getTitle())
                .activityType(request.getActivityType())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .notes(request.getNotes())
                .opportunity(opportunity)
                .contact(contact)
                .build();

        Activity savedActivity = activityRepository.save(activity);
        return toDto(savedActivity);
    }

    @Override
    @Transactional
    public ActivityDto update(Long id, ActivityRequest request) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到活動 ID: " + id));
        updateEntityFromRequest(activity, request);
        Activity updatedActivity = activityRepository.save(activity);
        return toDto(updatedActivity);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!activityRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到要刪除的活動 ID: " + id);
        }
        activityRepository.deleteById(id);
    }

    private void updateEntityFromRequest(Activity activity, ActivityRequest request) {
        activity.setTitle(request.getTitle());
        activity.setActivityType(request.getActivityType());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setNotes(request.getNotes());

        if (request.getOpportunityId() != null) {
            activity.setOpportunity(opportunityRepository.findById(request.getOpportunityId())
                    .orElseThrow(() -> new EntityNotFoundException("關聯商機失敗：找不到商機 ID " + request.getOpportunityId())));
        } else {
            activity.setOpportunity(null);
        }

        if (request.getContactId() != null) {
            activity.setContact(contactRepository.findById(request.getContactId())
                    .orElseThrow(() -> new EntityNotFoundException("關聯聯絡人失敗：找不到聯絡人 ID " + request.getContactId())));
        } else {
            activity.setContact(null);
        }
    }

    private ActivityDto toDto(Activity activity) {
        return new ActivityDto(
                activity.getId(),
                activity.getTitle(),
                activity.getActivityType(),
                activity.getStartTime(),
                activity.getEndTime(),
                activity.getStatus(),
                activity.getNotes(),
                activity.getOpportunity() != null ? activity.getOpportunity().getOpportunityId() : null,
                activity.getOpportunity() != null ? activity.getOpportunity().getOpportunityName() : null,
                activity.getContact() != null ? activity.getContact().getContactId() : null,
                activity.getContact() != null ? activity.getContact().getContactName() : null
        );
    }

}
