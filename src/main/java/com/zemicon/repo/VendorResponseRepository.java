package com.zemicon.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zemicon.model.Requirement;
import com.zemicon.model.Vendor;
import com.zemicon.model.VendorResponse;

public interface VendorResponseRepository extends JpaRepository<VendorResponse, Long> {
    Optional<VendorResponse> findByRequirementAndVendor(Requirement requirement, Vendor vendor);
    List<VendorResponse> findByRequirement(Requirement requirement);
    List<VendorResponse> findByRequirementIdIn(List<Long> requirementIds);
    // helper for vendor+customer view:
    List<VendorResponse> findByRequirement_Customer_IdAndVendor_IdOrderBySubmittedAtDesc(Long customerId, Long vendorId);
    List<VendorResponse> findByRequirement_Customer_IdOrderByRequirement_MpnAscSubmittedAtDesc(Long customerId);
}
