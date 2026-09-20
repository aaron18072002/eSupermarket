package com.coding.repository;

import com.coding.model.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductGroupRepository extends JpaRepository<ProductGroup, UUID> {

    Optional<ProductGroup> findByName(String name);

    boolean existsByName(String name);

}
