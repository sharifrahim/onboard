package com.github.sharifrahim.onboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sharifrahim.onboard.domain.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
