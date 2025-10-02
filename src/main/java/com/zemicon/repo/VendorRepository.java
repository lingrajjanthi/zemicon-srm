package com.zemicon.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.zemicon.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByName(String name);
}
