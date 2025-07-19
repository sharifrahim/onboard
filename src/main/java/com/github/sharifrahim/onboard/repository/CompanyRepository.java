package com.github.sharifrahim.onboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sharifrahim.onboard.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
