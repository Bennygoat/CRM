package com.example.demo.repository;

import com.example.demo.entity.OpportunityTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpportunityTagRepository extends JpaRepository<OpportunityTag, Long> {

    Optional<OpportunityTag> findByTagName(String tagName);

}
