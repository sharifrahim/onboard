package com.github.sharifrahim.onboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sharifrahim.onboard.domain.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByType(Approval.Type type);

    List<Approval> findByTypeAndApprovalStatus(Approval.Type type, Approval.ApprovalStatus status);
}
