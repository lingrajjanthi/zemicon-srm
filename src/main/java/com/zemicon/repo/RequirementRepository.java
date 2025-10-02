package com.zemicon.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zemicon.model.Customer;
import com.zemicon.model.Requirement;
import com.zemicon.model.RequirementStatus;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    Optional<Requirement> findByCustomerAndMpn(Customer customer, String mpn);
    Optional<Requirement> findByCustomer_IdAndMpn(Long customerId, String mpn);
    List<Requirement> findByCustomer_Id(Long customerId);
    List<Requirement> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);
    List<Requirement> findByCustomerAndStatus(Customer customer, RequirementStatus status);
}