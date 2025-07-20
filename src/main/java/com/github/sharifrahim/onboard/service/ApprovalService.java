package com.github.sharifrahim.onboard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.repository.ApprovalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;

    public Approval save(Approval approval) {
        return approvalRepository.save(approval);
    }

    public Optional<Approval> findById(Long id) {
        return approvalRepository.findById(id);
    }

    public List<Approval> findAll() {
        return approvalRepository.findAll();
    }
}
