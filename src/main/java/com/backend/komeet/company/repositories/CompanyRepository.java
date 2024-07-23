package com.backend.komeet.company.repositories;

import com.backend.komeet.company.model.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompanyManagerUserSeq(Long userSeq);
}
